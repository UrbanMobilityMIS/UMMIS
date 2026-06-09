package com.group22.mobility.controller;

import com.group22.mobility.dto.MongoTechnicianUseCaseDto;
import com.group22.mobility.dto.OnboardTechnicianDto;
import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.model.mongodb.TechnicianMaintenanceDocument;
import com.group22.mobility.service.Student3MongoService;
import com.group22.mobility.service.Student3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor

@RequestMapping("/student3")
public class Student3Controller {

    private final Student3Service student3Service;
    private final Student3MongoService student3MongoService;

    // ==========================
    // MariaDB Use Case
    // ==========================

    @GetMapping("/usecase")
    public String useCase(Model model) {

        model.addAttribute("dto", new OnboardTechnicianDto());
        model.addAttribute("vehicles", student3Service.getAllVehicles());

        return "student3/usecase";
    }

    @PostMapping("/usecase")
    public String submitUseCase(OnboardTechnicianDto dto,
            RedirectAttributes redirectAttributes) {

        MaintenanceLog log = student3Service.onboardTechnician(dto);

        redirectAttributes.addFlashAttribute(
                "success",
                "Level 2 technician onboarded successfully. Maintenance Log #" +
                        log.getLogNumber() +
                        " created successfully.");

        return "redirect:/student3/usecase";
    }

    @GetMapping("analytics")
    public String analytics(Model model) {

        model.addAttribute(
                "rows",
                student3Service.getAnalytics());

        return "student3/analytics";
    }

    // ==========================
    // MongoDB Direct Use Case
    // ==========================

    @GetMapping("/mongo-usecase")
    public String mongoUseCase(Model model) {

        model.addAttribute("dto", new MongoTechnicianUseCaseDto());

        return "student3/mongo-usecase";
    }

    @PostMapping("/mongo-usecase")
    public String submitMongoUseCase(MongoTechnicianUseCaseDto dto,
            RedirectAttributes redirectAttributes) {

        TechnicianMaintenanceDocument document = student3MongoService.createMongoTechnicianMaintenance(dto);

        redirectAttributes.addFlashAttribute(
                "success",
                "Level 2 technician maintenance document created directly in MongoDB.");

        return "redirect:/student3/mongo-usecase";
    }

    // ==========================
    // MongoDB Migration
    // ==========================

    @GetMapping("/mongo-migration")
    public String mongoMigrationPage() {
        return "student3/mongo-migration";
    }

    @PostMapping("/mongo-migrate")
    public String migrateToMongo(RedirectAttributes redirectAttributes) {

        student3MongoService.migrateStudent3DataToMongo();

        redirectAttributes.addFlashAttribute(
                "success",
                "Student 3 data migrated from MariaDB to MongoDB successfully.");

        return "redirect:/student3/mongo-analytics";
    }

    // ==========================
    // MongoDB Analytics
    // ==========================

    @GetMapping("/mongo-analytics")
    public String mongoAnalytics(Model model) {

        model.addAttribute(
                "rows",
                student3MongoService.getLevel2TechnicianReport());

        return "student3/mongo-analytics";
    }
}