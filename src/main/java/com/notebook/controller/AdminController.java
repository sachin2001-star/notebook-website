package com.notebook.controller;

import com.notebook.ContactSubmission;
import com.notebook.ContactSubmissionRepository;
import com.notebook.VisitorLog;
import com.notebook.VisitorLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ContactSubmissionRepository contactSubmissionRepository;
    
    @Autowired
    private VisitorLoggingService visitorLoggingService;

    @GetMapping("/login")
    public String adminLogin(@RequestParam(value = "error", required = false) String error, 
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been successfully logged out.");
        }
        return "admin-login";
    }

    @GetMapping("/logout")
    public String adminLogout() {
        return "redirect:/";
    }

    @GetMapping("")
    public String adminHome(Model model) {
        List<ContactSubmission> allSubmissions = contactSubmissionRepository.findAll();
        
        // Sort by most recent first
        allSubmissions.sort((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()));
        
        model.addAttribute("submissions", allSubmissions);
        
        // Add contact submission statistics
        long totalSubmissions = allSubmissions.size();
        long submissionsWithFiles = allSubmissions.stream()
                .filter(s -> s.getFileData() != null && s.getFileData().length > 0)
                .count();
        long todaySubmissions = allSubmissions.stream()
                .filter(s -> s.getSubmittedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count();
        long weekSubmissions = allSubmissions.stream()
                .filter(s -> s.getSubmittedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
        
        model.addAttribute("totalSubmissions", totalSubmissions);
        model.addAttribute("submissionsWithFiles", submissionsWithFiles);
        model.addAttribute("todaySubmissions", todaySubmissions);
        model.addAttribute("weekSubmissions", weekSubmissions);
        
        // Add visitor analytics
        model.addAttribute("totalVisits", visitorLoggingService.getTotalVisits());
        model.addAttribute("todayVisits", visitorLoggingService.getTodayVisits());
        model.addAttribute("weekVisits", visitorLoggingService.getWeekVisits());
        model.addAttribute("monthVisits", visitorLoggingService.getMonthVisits());
        model.addAttribute("uniqueVisitorsToday", visitorLoggingService.getUniqueVisitorsToday().size());
        
        return "admin-panel";
    }

    @GetMapping("/analytics")
    public String visitorAnalytics(Model model) {
        // Get visitor analytics data
        model.addAttribute("totalVisits", visitorLoggingService.getTotalVisits());
        model.addAttribute("todayVisits", visitorLoggingService.getTodayVisits());
        model.addAttribute("weekVisits", visitorLoggingService.getWeekVisits());
        model.addAttribute("monthVisits", visitorLoggingService.getMonthVisits());
        model.addAttribute("uniqueVisitorsToday", visitorLoggingService.getUniqueVisitorsToday().size());
        
        // Get detailed analytics
        model.addAttribute("visitsByPage", visitorLoggingService.getVisitsByPage());
        model.addAttribute("visitsByDeviceType", visitorLoggingService.getVisitsByDeviceType());
        model.addAttribute("visitsByBrowser", visitorLoggingService.getVisitsByBrowser());
        model.addAttribute("visitsByOperatingSystem", visitorLoggingService.getVisitsByOperatingSystem());
        model.addAttribute("recentVisits", visitorLoggingService.getRecentVisits());
        
        return "visitor-analytics";
    }

    @PostMapping("/delete/{id}")
    public String deleteSubmission(@PathVariable Long id) {
        contactSubmissionRepository.deleteById(id);
        return "redirect:/admin";
    }
    
    @GetMapping("/stats")
    @ResponseBody
    public Map<String, Object> getStats() {
        List<ContactSubmission> allSubmissions = contactSubmissionRepository.findAll();
        
        Map<String, Object> stats = Map.of(
            "totalSubmissions", allSubmissions.size(),
            "submissionsWithFiles", allSubmissions.stream()
                .filter(s -> s.getFileData() != null && s.getFileData().length > 0)
                .count(),
            "todaySubmissions", allSubmissions.stream()
                .filter(s -> s.getSubmittedAt().toLocalDate().equals(LocalDateTime.now().toLocalDate()))
                .count(),
            "weekSubmissions", allSubmissions.stream()
                .filter(s -> s.getSubmittedAt().isAfter(LocalDateTime.now().minusDays(7)))
                .count(),
            "monthSubmissions", allSubmissions.stream()
                .filter(s -> s.getSubmittedAt().isAfter(LocalDateTime.now().minusDays(30)))
                .count(),
            "totalVisits", visitorLoggingService.getTotalVisits(),
            "todayVisits", visitorLoggingService.getTodayVisits(),
            "weekVisits", visitorLoggingService.getWeekVisits(),
            "monthVisits", visitorLoggingService.getMonthVisits(),
            "uniqueVisitorsToday", visitorLoggingService.getUniqueVisitorsToday().size()
        );
        
        return stats;
    }
    
    @GetMapping("/search")
    public String searchSubmissions(@RequestParam String query, Model model) {
        List<ContactSubmission> allSubmissions = contactSubmissionRepository.findAll();
        
        List<ContactSubmission> filteredSubmissions = allSubmissions.stream()
            .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase()) ||
                        s.getEmail().toLowerCase().contains(query.toLowerCase()) ||
                        s.getSubject().toLowerCase().contains(query.toLowerCase()) ||
                        s.getMessage().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());
        
        model.addAttribute("submissions", filteredSubmissions);
        model.addAttribute("searchQuery", query);
        return "admin-panel";
    }
    
    @GetMapping("/export")
    @ResponseBody
    public String exportSubmissions() {
        List<ContactSubmission> submissions = contactSubmissionRepository.findAll();
        
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Name,Email,Phone,Subject,Message,FileName,SubmittedAt\n");
        
        for (ContactSubmission submission : submissions) {
            csv.append(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                submission.getId(),
                submission.getName().replace("\"", "\"\""),
                submission.getEmail(),
                submission.getPhone(),
                submission.getSubject().replace("\"", "\"\""),
                submission.getMessage().replace("\"", "\"\""),
                submission.getFileName() != null ? submission.getFileName().replace("\"", "\"\"") : "",
                submission.getSubmittedAt()
            ));
        }
        
        return csv.toString();
    }
} 