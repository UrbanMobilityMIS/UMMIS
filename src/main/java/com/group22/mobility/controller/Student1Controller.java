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

    // ── Use Case page ─────────────────────────────────────────

    @GetMapping("/usecase")
    public String useCasePage(Model model) {
        model.addAttribute("form", new MaintenanceLogDto());
        model.addAttribute("vehicles",    service.getAllVehicles());
        model.addAttribute("technicians", service.getAllTechnicians());
        return "student1/usecase";
    }

    @PostMapping("/usecase")
    public String submitUseCase(@Valid @ModelAttribute("form") MaintenanceLogDto form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("vehicles",    service.getAllVehicles());
            model.addAttribute("technicians", service.getAllTechnicians());
            return "student1/usecase";
        }
        try {
            var log = service.reportDamage(form);
            redirect.addFlashAttribute("success",
                    "Maintenance Log #" + log.getLogNumber() + " created for Vehicle ID " + log.getVehicle().getId());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student1/usecase";
    }

    // ── Analytics page ────────────────────────────────────────

    @GetMapping("/analytics")
    public String analyticsPage(Model model) {
        model.addAttribute("rows", service.getMaintenanceCostReport());
        return "student1/analytics";
    }
}
