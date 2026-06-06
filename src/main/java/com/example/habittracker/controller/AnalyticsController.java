package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.service.AnalyticsService;
import com.example.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private UserService userService;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping
    public String analyticsDashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();

        // Get analytics data
        Map<String, Object> analyticsData = analyticsService.getUserAnalytics(user);
        
        model.addAttribute("analytics", analyticsData);
        model.addAttribute("user", user);
        
        return "analytics";
    }

    @GetMapping("/habit/{habitId}")
    public String habitAnalytics(@PathVariable Long habitId, 
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                               @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                               Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();

        if (startDate == null) startDate = LocalDate.now().minusDays(30);
        if (endDate == null) endDate = LocalDate.now();

        Map<String, Object> habitAnalytics = analyticsService.getHabitAnalytics(habitId, user, startDate, endDate);
        
        model.addAttribute("habitAnalytics", habitAnalytics);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("user", user);
        
        return "habit-analytics";
    }

    @GetMapping("/export")
    public String exportData(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();

        model.addAttribute("user", user);
        return "export";
    }
} 