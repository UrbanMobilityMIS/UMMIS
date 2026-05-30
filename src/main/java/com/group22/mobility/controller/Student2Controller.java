package com.group22.mobility.controller;

import com.group22.mobility.repository.mariadb.UserRepository;
import com.group22.mobility.repository.mariadb.VehicleRepository;
import com.group22.mobility.service.Student2Service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/student2")
public class Student2Controller {

    private final Student2Service student2Service;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    public Student2Controller(
            Student2Service student2Service,
            UserRepository userRepository,
            VehicleRepository vehicleRepository
    ) {
        this.student2Service = student2Service;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
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

        // All users
        model.addAttribute(
                "users",
                userRepository.findAll()
        );

        // Show ALL vehicles
        model.addAttribute(
                "vehicles",
                vehicleRepository.findAll()
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

        String message =
                student2Service.rentVehicle(userId, vehicleId);

        redirectAttributes.addFlashAttribute(
                "message",
                message
        );

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

        model.addAttribute(
                "rentals",
                student2Service.getFilteredRentals(fromDate, toDate)
        );

        model.addAttribute(
                "totalRentals",
                student2Service.getFilteredTotalRentals(fromDate, toDate)
        );

        model.addAttribute(
                "mostActiveUser",
                student2Service.getFilteredMostActiveUser(fromDate, toDate)
        );

        model.addAttribute(
                "mostRentedVehicle",
                student2Service.getFilteredMostRentedVehicle(fromDate, toDate)
        );

        model.addAttribute(
                "rentalsPerVehicle",
                student2Service.getFilteredRentalsPerVehicle(fromDate, toDate)
        );

        return "student2/analytics";
    }
}