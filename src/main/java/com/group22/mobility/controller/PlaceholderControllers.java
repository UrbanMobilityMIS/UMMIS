package com.group22.mobility.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student2")
class Student2Controller {
    @GetMapping("/usecase")   public String uc()        { return "student2/usecase"; }
    @GetMapping("/analytics") public String analytics() { return "student2/analytics"; }
}

@Controller
@RequestMapping("/student3")
class Student3Controller {
    @GetMapping("/usecase")   public String uc()        { return "student3/usecase"; }
    @GetMapping("/analytics") public String analytics() { return "student3/analytics"; }
}
