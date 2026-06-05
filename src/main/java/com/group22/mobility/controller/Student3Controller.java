package com.group22.mobility.controller;

import com.group22.mobility.dto.OnboardTechnicianDto;
import com.group22.mobility.model.MaintenanceLog;
import com.group22.mobility.service.Student3MongoService;
import com.group22.mobility.service.Student3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class Student3Controller {

    private final Student3Service student3Service;
    private final Student3MongoService student3MongoService;

    // MariaDB use case page
    @GetMapping("/student3/usecase")
    public String useCase(Model model) {
        model.addAttribute("dto", new OnboardTechnicianDto());
        model.addAttribute("vehicles", student3Service.getAllVehicles());
        return "student3/usecase";
    }

    // MariaDB use case submit
    @PostMapping("/student3/usecase")
    public String submitUseCase(OnboardTechnicianDto dto,
                                RedirectAttributes redirectAttributes) {

        MaintenanceLog log = student3Service.onboardTechnician(dto);

        redirectAttributes.addFlashAttribute(
                "success",
                "Level 2 technician onboarded successfully. Maintenance Log #" +
                        log.getLogNumber() +
                        " created successfully."
        );

        return "redirect:/student3/usecase";
    }

    // MariaDB analytics
    @GetMapping("/student3/analytics")
    public String analytics(Model model) {
        model.addAttribute("rows", student3Service.getAnalytics());
        return "student3/analytics";
    }

    // MongoDB use case page
    @GetMapping("/student3/mongo/usecase")
    public String mongoUseCase() {
        return "student3/mongo-usecase";
    }

    // MongoDB migration action
    @PostMapping("/student3/mongo/migrate")
    public String migrateToMongo(RedirectAttributes redirectAttributes) {

        student3MongoService.migrateStudent3DataToMongo();

        redirectAttributes.addFlashAttribute(
                "success",
                "Student 3 data migrated from MariaDB to MongoDB successfully."
        );

        return "redirect:/student3/mongo/analytics";
    }

    // MongoDB analytics
    @GetMapping("/student3/mongo/analytics")
    public String mongoAnalytics(Model model) {
        model.addAttribute("rows", student3MongoService.getLevel2TechnicianReport());
        return "student3/mongo-analytics";
    }
}