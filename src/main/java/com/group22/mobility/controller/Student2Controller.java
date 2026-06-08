package com.group22.mobility.controller;
import com.group22.mobility.repository.mariadb.UserRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.repository.mariadb.mangodb.RentalMongoRepository;
import com.group22.mobility.repository.mariadb.mangodb.VehicleMongoRepository;
import com.group22.mobility.model.mangodb.VehicleDocument;
import com.group22.mobility.service.Student2Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.group22.mobility.model.Rental;
import com.group22.mobility.model.Vehicle;
import com.group22.mobility.repository.mariadb.RentalRepository;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;
import com.group22.mobility.model.mangodb.UserDocument;
import com.group22.mobility.repository.mariadb.mangodb.UserMongoRepository;
import com.group22.mobility.model.User;
import com.group22.mobility.model.mangodb.RentalDocument;

@Controller
@RequestMapping("/student2")
public class Student2Controller {

    private final Student2Service student2Service;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleMongoRepository vehicleMongoRepository;
    private final RentalMongoRepository rentalMongoRepository;
    private final RentalRepository rentalRepository;
    private final UserMongoRepository userMongoRepository;

        public Student2Controller(
                Student2Service student2Service,
                UserRepository userRepository,
                VehicleRepository vehicleRepository,
                RentalMongoRepository rentalMongoRepository,
                VehicleMongoRepository vehicleMongoRepository,
                UserMongoRepository userMongoRepository,
                RentalRepository rentalRepository
        ) {
        this.student2Service = student2Service;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalMongoRepository = rentalMongoRepository;
        this.vehicleMongoRepository = vehicleMongoRepository;
        this.userMongoRepository = userMongoRepository;
        this.rentalRepository = rentalRepository;
        }
    

    // =========================
    // USE CASE PAGE
    // =========================

         @GetMapping("/mongodb-usecase")
        public String mongoUseCase(Model model) {

                model.addAttribute(
                        "users",
                        userMongoRepository.findAll()
                );

                model.addAttribute(
                        "vehicles",
                        vehicleMongoRepository.findAll()
                                .stream()
                                .filter(VehicleDocument::isAvailable)
                                .toList()
                );

        return "student2/mongodb-usecase";
} 

                @GetMapping("/usecase")
        public String useCase(Model model) {

        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        model.addAttribute(
                "vehicles",
                vehicleRepository.findAll()
                        .stream()
                        .filter(Vehicle::isAvailable)
                        .toList()
        );

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
        @PostMapping("/return-rental/{id}")
        public String returnVehicle(
        @PathVariable Integer id,
        RedirectAttributes redirectAttributes
        ) {

        String message =
                student2Service.returnVehicle(id);

        redirectAttributes.addFlashAttribute(
                "message",
                message
        );

        return "redirect:/student2/analytics";

        }
         @PostMapping("/mongo-return-rental/{id}")
        public String returnVehicleMongo(
                @PathVariable Integer id,
                RedirectAttributes redirectAttributes
        ) {

        String message =
                student2Service.returnVehicleMongo(id);

        redirectAttributes.addFlashAttribute(
                "message",
                message
        );

        return "redirect:/student2/mongo-analytics";
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
                model.addAttribute(
        "activeRentals",
        student2Service.getActiveRentals()
         );
         model.addAttribute(
          "vehicles",
          student2Service.getAllVehicles()
                        );
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
        public String runMigration(
                RedirectAttributes redirectAttributes
    ) {

    try {

        // Clear MongoDB collections

        userMongoRepository.deleteAll();
        vehicleMongoRepository.deleteAll();
        rentalMongoRepository.deleteAll();

        // =========================
        // MIGRATE USERS
        // =========================

        var users = userRepository.findAll();

        for (User user : users) {

        UserDocument uDoc = new UserDocument();

        uDoc.setUserId(user.getId());
        uDoc.setEmailAddress(user.getEmailAddress());
        uDoc.setPaymentMethod(user.getPaymentMethod());

        userMongoRepository.save(uDoc);
        }

        // =========================
        // MIGRATE VEHICLES
        // =========================

        var vehicles = vehicleRepository.findAll();

        for (Vehicle vehicle : vehicles) {

            VehicleDocument vDoc = new VehicleDocument();

            vDoc.setVehicleId(vehicle.getId());
            vDoc.setVin(vehicle.getVin());
            vDoc.setBatteryLevel(vehicle.getBatteryLevel());
            vDoc.setModelType(vehicle.getModelType());
            vDoc.setAvailable(vehicle.isAvailable());

            vehicleMongoRepository.save(vDoc);
        }

        // =========================
        // MIGRATE RENTALS
        // =========================

        var rentals = rentalRepository.findByEndTimeIsNull();

        for (Rental rental : rentals) {

            RentalDocument doc = new RentalDocument();

            doc.setRentalId(rental.getId());

            doc.setUserId(
                    rental.getUser().getId()
            );

            doc.setUserEmail(
                    rental.getUser().getEmailAddress()
            );

            doc.setVehicleId(
                    rental.getVehicle().getId()
            );

            doc.setVehicleModel(
                    rental.getVehicle().getModelType()
            );

            doc.setStartTime(
                    rental.getStartTime()
            );
            doc.setEndTime(
                    rental.getEndTime()
            );

            rentalMongoRepository.save(doc);
        }

        redirectAttributes.addFlashAttribute(
                       "success",
                         users.size()
                        + " users, "
                        + vehicles.size()
                        + " vehicles and "
                        + rentals.size()
                        + " rentals migrated successfully."
        );

    } catch (Exception e) {

        e.printStackTrace();

        redirectAttributes.addFlashAttribute(
                "error",
                "Migration failed: "
                        + e.getMessage()
        );
    }

    return "redirect:/student2/migrate";
}

    // =========================
    // MONGO RENT VEHICLE
    // ===============

       @PostMapping("/mongo-rent")
       public String mongoRent(
        @RequestParam Integer userId,
        @RequestParam Integer vehicleId,
        RedirectAttributes redirectAttributes) {

         String message =
            student2Service.rentVehicleMongo(userId, vehicleId);

    redirectAttributes.addFlashAttribute("message", message);

    return "redirect:/student2/mongodb-usecase";
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
        var mongoRentals = rentalMongoRepository.findAll()
        .stream()
        .filter(r -> r.getEndTime() == null)
        .collect(Collectors.toList());

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
        Map<String, Long> userCounts =
        mongoRentals.stream()
                        .collect(Collectors.groupingBy(
                                r -> r.getUserEmail(),
                                Collectors.counting()
                        ));

        long maxUserCount =
                userCounts.values()
                        .stream()
                        .max(Long::compare)
                        .orElse(0L);

        String mostActiveUser =
                userCounts.entrySet()
                        .stream()
                        .filter(e -> e.getValue().equals(maxUserCount))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "));

        if (mostActiveUser.isEmpty()) {
        mostActiveUser = "No Data";
        }
                model.addAttribute("mostActiveMongoUser", mostActiveUser);

        // Most rented vehicle
        Map<String, Long> vehicleCounts =
        mongoRentals.stream()
                 .collect(Collectors.groupingBy(
                                r -> r.getVehicleModel(),
                                Collectors.counting()
                        ));

        long maxVehicleCount =
                vehicleCounts.values()
                        .stream()
                        .max(Long::compare)
                        .orElse(0L);

        String mostRentedVehicle =
                vehicleCounts.entrySet()
                        .stream()
                        .filter(e -> e.getValue().equals(maxVehicleCount))
                        .map(Map.Entry::getKey)
                        .collect(Collectors.joining(", "));

        if (mostRentedVehicle.isEmpty()) {
        mostRentedVehicle = "No Data";
        }
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
        model.addAttribute(
        "vehicles",
        vehicleMongoRepository.findAll()
);

        return "student2/mongo-analytics";
    }
}