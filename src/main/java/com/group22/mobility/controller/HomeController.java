package com.group22.mobility.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.group22.mobility.service.MariaDbSeedService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MariaDbSeedService mariaDbSeedService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/admin/reseed-mariadb")
    public String reseedMariaDb(RedirectAttributes redirectAttributes) {
        try {
            mariaDbSeedService.resetAndSeed();
            redirectAttributes.addFlashAttribute("success", "MariaDB sample data was reset and loaded successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "MariaDB reseed failed: " + e.getMessage());
        }
        return "redirect:/";
    }
}
