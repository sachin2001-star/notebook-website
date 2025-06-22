package com.notebook.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/error-404")
    public String trigger404() {
        // This will never be reached, but helps with testing
        return "redirect:/nonexistent-page";
    }

    @GetMapping("/error-500")
    public String trigger500() {
        throw new RuntimeException("This is a test error for 500 status");
    }

    @GetMapping("/error-403")
    public String trigger403() {
        throw new RuntimeException("This is a test error for 403 status");
    }
} 