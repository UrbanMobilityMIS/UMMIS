package com.group22.mobility.controller;

import com.group22.mobility.dto.MaintenanceLogDto;
import com.group22.mobility.service.Student1Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student1")
@RequiredArgsConstructor
public class Student1Controller {

    private final Student1Service service;

    // ════════════════════════════════════════════════════════
    // MARIADB endpoints
    // ════════════════════════════════════════════════════════

    @GetMapping("/usecase")
    public String useCasePage(Model model) {
        model.addAttribute("form", new MaintenanceLogDto());
        model.addAttribute("vehicles", service.getAllVehicles());
        model.addAttribute("technicians", service.getAllTechnicians());
        return "student1/usecase";
    }

    @PostMapping("/usecase")
    public String submitUseCase(@Valid @ModelAttribute("form") MaintenanceLogDto form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("vehicles", service.getAllVehicles());
            model.addAttribute("technicians", service.getAllTechnicians());
            return "student1/usecase";
        }
        try {
            var log = service.reportDamage(form);
            redirect.addFlashAttribute("success",
                    "Maintenance Log #" + log.getLogNumber()
                            + " created for Vehicle ID " + log.getVehicle().getId());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student1/usecase";
    }

    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        model.addAttribute("rows", service.getMaintenanceCostReport());
        return "student1/analytics";
    }

    // ════════════════════════════════════════════════════════
    // MONGODB Migration (New Dedicated Section)
    // ════════════════════════════════════════════════════════

    /** Show the dedicated migration page */
    @GetMapping("/migrate")
    public String migrationPage(Model model) {
        model.addAttribute("docCount", service.getMongoDocumentCount());
        return "student1/migration";
    }

    /** Trigger migration from MariaDB → MongoDB */
    @PostMapping("/migrate")
    public String migrate(RedirectAttributes redirect) {
        try {
            int count = service.migrateToMongo();
            redirect.addFlashAttribute("success",
                    "Migration complete! " + count + " vehicle documents inserted into MongoDB.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Migration failed: " + e.getMessage());
        }
        // Redirect back to the migration page to show success message
        return "redirect:/student1/migrate";
    }

    // ════════════════════════════════════════════════════════
    // MONGODB endpoints
    // ════════════════════════════════════════════════════════

    @GetMapping("/mongo-usecase")
    public String mongoUseCasePage(Model model) {
        model.addAttribute("form", new MaintenanceLogDto());
        model.addAttribute("vehicles", service.getAllVehicles());
        model.addAttribute("technicians", service.getAllTechnicians());
        // We keep docCount here so the UI can still warn if migration is missing
        model.addAttribute("docCount", service.getMongoDocumentCount());
        return "student1/mongo-usecase";
    }

    @PostMapping("/mongo-usecase")
    public String submitMongoUseCase(@Valid @ModelAttribute("form") MaintenanceLogDto form,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("vehicles", service.getAllVehicles());
            model.addAttribute("technicians", service.getAllTechnicians());
            model.addAttribute("docCount", service.getMongoDocumentCount());
            return "student1/mongo-usecase";
        }
        try {
            var doc = service.reportDamageMongo(form);
            redirect.addFlashAttribute("success",
                    "Log added to MongoDB document for VIN: " + doc.getVin()
                            + " (total logs: " + doc.getMaintenanceLogs().size() + ")");
        } catch (IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student1/mongo-usecase";
    }

    @GetMapping("/mongo-analytics")
    public String mongoAnalyticsPage(Model model) {
        model.addAttribute("rows", service.getMongoMaintenanceCostReport());
        model.addAttribute("docCount", service.getMongoDocumentCount());
        return "student1/mongo-analytics";
    }
}