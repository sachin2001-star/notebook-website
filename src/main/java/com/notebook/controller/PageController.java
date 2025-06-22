package com.notebook.controller;

import com.notebook.VisitorLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PageController {

    @Autowired
    private VisitorLoggingService visitorLoggingService;

    @GetMapping("/")
    public String home() {
        visitorLoggingService.logVisitor("Home");
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        visitorLoggingService.logVisitor("About");
        return "about";
    }

    @GetMapping("/gallery")
    public String gallery() {
        visitorLoggingService.logVisitor("Gallery");
        return "gallery";
    }

    // Simple test endpoint
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Application is working! Test endpoint successful.";
    }

    // Database test endpoint
    @GetMapping("/test-db-simple")
    @ResponseBody
    public String testDatabaseSimple() {
        return "Database test endpoint is accessible!";
    }
}
