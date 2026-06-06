package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.service.CheckInService;
import com.example.habittracker.service.HabitService;
import com.example.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private HabitService habitService;

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();

        List<Habit> habits = habitService.findByUser(user);
        
        // Check which habits have been checked in today
        Map<Long, Boolean> checkedInToday = habits.stream()
                .collect(Collectors.toMap(
                    Habit::getId,
                    checkInService::hasCheckedInToday
                ));

        // Calculate completed today count
        long completedToday = habits.stream()
                .filter(habit -> checkInService.hasCheckedInToday(habit))
                .count();

        // Calculate best streak
        int bestStreak = habits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .max()
                .orElse(0);

        model.addAttribute("habits", habits);
        model.addAttribute("checkedInToday", checkedInToday);
        model.addAttribute("user", user);
        model.addAttribute("completedToday", completedToday);
        model.addAttribute("bestStreak", bestStreak);

        return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}


