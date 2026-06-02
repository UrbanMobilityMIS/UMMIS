package com.group22.mobility.controller;

import com.group22.mobility.repository.mariadb.UserRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.repository.mariadb.mangodb.RentalMongoRepository;
import com.group22.mobility.service.Student2Service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group22.mobility.model.Rental;
import com.group22.mobility.model.mangodb.RentalDocument;
import com.group22.mobility.repository.mariadb.RentalRepository;

import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student2")
public class Student2Controller {

    private final Student2Service student2Service;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalMongoRepository rentalMongoRepository;
    private final RentalRepository rentalRepository;

    public Student2Controller(
            Student2Service student2Service,
            UserRepository userRepository,
            VehicleRepository vehicleRepository,
            RentalMongoRepository rentalMongoRepository,
            RentalRepository rentalRepository
    ) {
        this.student2Service = student2Service;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalMongoRepository = rentalMongoRepository;
        this.rentalRepository = rentalRepository;
    }

    @GetMapping({"", "/"})
    public String homeRedirect() {
        return "redirect:/student2/usecase";
    }
    

    // =========================
    // USE CASE PAGE
    // =========================

    @GetMapping("/usecase")
    public String useCasePage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        return "student2/usecase";
    }

    // =========================
    // RENT VEHICLE
    // =========================

    @PostMapping("/rent")
    public String rentVehicle(
            @RequestParam Integer userId,
            @RequestParam Integer vehicleId,
            RedirectAttributes redirectAttributes
    ) {
        String message = student2Service.rentVehicle(userId, vehicleId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/student2/usecase";
    }

    // =========================
    // ANALYTICS PAGE
    // =========================

    @GetMapping("/analytics")
    public String analyticsPage(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            Model model
    ) {
        model.addAttribute("rentals",
                student2Service.getFilteredRentals(fromDate, toDate));
        model.addAttribute("totalRentals",
                student2Service.getFilteredTotalRentals(fromDate, toDate));
        model.addAttribute("mostActiveUser",
                student2Service.getFilteredMostActiveUser(fromDate, toDate));
        model.addAttribute("mostRentedVehicle",
                student2Service.getFilteredMostRentedVehicle(fromDate, toDate));
        model.addAttribute("rentalsPerVehicle",
                student2Service.getFilteredRentalsPerVehicle(fromDate, toDate));
        return "student2/analytics";
    }

    // =========================
    // MIGRATE TO MONGODB
    // =========================

    @GetMapping("/migrate")
    public String showMigratePage() {
        return "student2/migrate";
    }

    @PostMapping("/migrate")
    public String runMigration(RedirectAttributes redirectAttributes) {
        try {
            // Clear existing MongoDB data first
            rentalMongoRepository.deleteAll();

            // Read all rentals from MariaDB
            var rentals = rentalRepository.findAll();

            for (Rental rental : rentals) {
                RentalDocument doc = new RentalDocument();
                doc.setRentalId(rental.getId());
                doc.setUserId(rental.getUser().getId());
                doc.setUserEmail(rental.getUser().getEmailAddress());
                doc.setVehicleId(rental.getVehicle().getId());
                doc.setVehicleModel(rental.getVehicle().getModelType());
                doc.setStartTime(rental.getStartTime());
                rentalMongoRepository.save(doc);
            }

            redirectAttributes.addFlashAttribute("success",
                    rentals.size() + " rentals migrated to MongoDB successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    "Migration failed: " + e.getMessage());
        }

        return "redirect:/student2/migrate";
    }

    // =========================
    // MONGO USE CASE
    // =========================

    @GetMapping("/mongo-usecase")
    public String mongoUseCase(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("vehicles", vehicleRepository.findAll());
        model.addAttribute("mongoRentals", rentalMongoRepository.findAll());
        return "student2/mongo-usecase";
    }

    @PostMapping("/mongo-rent")
    public String mongoRentVehicle(
            @RequestParam Integer userId,
            @RequestParam Integer vehicleId,
            RedirectAttributes redirectAttributes
    ) {
        String message = student2Service.rentVehicle(userId, vehicleId);
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/student2/mongo-usecase";
    }

    // =========================
    // MONGO ANALYTICS
    // =========================

    @GetMapping("/mongo-analytics")
    public String mongoAnalytics(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            Model model
    ) {
        var mongoRentals = rentalMongoRepository.findAll();

        // Filter by date if provided
        if (fromDate != null && toDate != null) {
            mongoRentals = mongoRentals.stream()
                    .filter(r -> r.getStartTime() != null)
                    .filter(r -> {
                        LocalDate d = r.getStartTime().toLocalDate();
                        return !d.isBefore(fromDate) && !d.isAfter(toDate);
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        // Total rentals
        model.addAttribute("totalMongoRentals", mongoRentals.size());

        // Most active user
        String mostActiveUser = mongoRentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getUserEmail(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No Data");
        model.addAttribute("mostActiveMongoUser", mostActiveUser);

        // Most rented vehicle
        String mostRentedVehicle = mongoRentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getVehicleModel(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No Data");
        model.addAttribute("mostRentedMongoVehicle", mostRentedVehicle);

        // Rentals per vehicle
        Map<String, Long> rentalsPerVehicle = mongoRentals.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        r -> r.getVehicleModel(),
                        java.util.stream.Collectors.counting()
                ));
        model.addAttribute("mongoRentalsPerVehicle", rentalsPerVehicle);

        model.addAttribute("mongoRentals", mongoRentals);
        model.addAttribute("fromDate", fromDate);
        model.addAttribute("toDate", toDate);

        return "student2/mongo-analytics";
    }
}