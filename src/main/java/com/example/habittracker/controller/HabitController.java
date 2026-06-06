package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.service.CheckInService;
import com.example.habittracker.service.HabitService;
import com.example.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @Autowired
    private UserService userService;

    @Autowired
    private CheckInService checkInService;

    @GetMapping("/new")
    public String showCreateForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        Habit habit = new Habit();
        habit.setUser(user);
        model.addAttribute("habit", habit);
        return "habit-form";
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String createHabit(@ModelAttribute Habit habit, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        
        habit.setUser(user);
        habitService.save(habit);
        
        redirectAttributes.addFlashAttribute("success", "Habit created successfully!");
        return "redirect:/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        
        Optional<Habit> habit = habitService.findById(id);
        if (habit.isPresent() && habit.get().getUser().getId().equals(user.getId())) {
            model.addAttribute("habit", habit.get());
            return "habit-form";
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/edit/{id}")
    public String updateHabit(@PathVariable Long id, @ModelAttribute Habit habit, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        
        Optional<Habit> existingHabit = habitService.findById(id);
        if (existingHabit.isPresent() && existingHabit.get().getUser().getId().equals(user.getId())) {
            Habit habitToUpdate = existingHabit.get();
            habitToUpdate.setName(habit.getName());
            habitToUpdate.setDescription(habit.getDescription());
            habitService.save(habitToUpdate);
            
            redirectAttributes.addFlashAttribute("success", "Habit updated successfully!");
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String deleteHabit(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        
        Optional<Habit> habit = habitService.findById(id);
        if (habit.isPresent() && habit.get().getUser().getId().equals(user.getId())) {
            habitService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Habit deleted successfully!");
        }
        
        return "redirect:/dashboard";
    }

    @PostMapping("/checkin/{id}")
    public String checkIn(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        
        Optional<Habit> habit = habitService.findById(id);
        if (habit.isPresent() && habit.get().getUser().getId().equals(user.getId())) {
            try {
                checkInService.checkIn(habit.get());
                redirectAttributes.addFlashAttribute("success", "Checked in successfully!");
            } catch (RuntimeException e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }
        
        return "redirect:/dashboard";
    }
}


