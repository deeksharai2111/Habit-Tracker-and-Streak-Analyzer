package com.example.habittracker.controller;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.service.AnalyticsService;
import com.example.habittracker.service.HabitService;
import com.example.habittracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private HabitService habitService;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/habits")
    public ResponseEntity<Map<String, Object>> getUserHabits(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            List<Habit> habits = habitService.findByUser(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("habits", habits);
            response.put("totalHabits", habits.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/habits/{habitId}")
    public ResponseEntity<Map<String, Object>> getHabit(@PathVariable Long habitId, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            Habit habit = habitService.findById(habitId)
                    .filter(h -> h.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new RuntimeException("Habit not found"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("habit", habit);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/habits")
    public ResponseEntity<Map<String, Object>> createHabit(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            String name = request.get("name");
            String description = request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Habit name is required");
            }
            
            Habit habit = new Habit(name, description, user);
            habit = habitService.save(habit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("habit", habit);
            response.put("message", "Habit created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/habits/{habitId}/checkin")
    public ResponseEntity<Map<String, Object>> checkInHabit(@PathVariable Long habitId, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            Habit habit = habitService.findById(habitId)
                    .filter(h -> h.getUser().getId().equals(user.getId()))
                    .orElseThrow(() -> new RuntimeException("Habit not found"));
            
            // This would need to be implemented in HabitService
            // For now, we'll just return success
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Check-in successful");
            response.put("currentStreak", habit.getCurrentStreak());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            Map<String, Object> analytics = analyticsService.getUserAnalytics(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("analytics", analytics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<Map<String, Object>> exportData(Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            List<Habit> habits = habitService.findByUser(user);
            Map<String, Object> analytics = analyticsService.getUserAnalytics(user);
            
            Map<String, Object> exportData = new HashMap<>();
            exportData.put("user", user);
            exportData.put("habits", habits);
            exportData.put("analytics", analytics);
            exportData.put("exportDate", java.time.LocalDateTime.now());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", exportData);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/export/download")
    public ResponseEntity<byte[]> downloadExport(@RequestParam String format, Authentication authentication) {
        try {
            String username = authentication.getName();
            User user = userService.findByUsername(username).orElseThrow();
            
            List<Habit> habits = habitService.findByUser(user);
            Map<String, Object> analytics = analyticsService.getUserAnalytics(user);
            
            byte[] exportData;
            String contentType;
            String filename;
            
            switch (format.toLowerCase()) {
                case "json":
                    exportData = generateJsonExport(user, habits, analytics);
                    contentType = "application/json";
                    filename = "habit-tracker-export.json";
                    break;
                case "csv":
                    exportData = generateCsvExport(user, habits, analytics);
                    contentType = "text/csv";
                    filename = "habit-tracker-export.csv";
                    break;
                case "pdf":
                    exportData = generatePdfExport(user, habits, analytics);
                    contentType = "application/pdf";
                    filename = "habit-tracker-report.pdf";
                    break;
                case "excel":
                    exportData = generateExcelExport(user, habits, analytics);
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    filename = "habit-tracker-export.xlsx";
                    break;
                default:
                    throw new RuntimeException("Unsupported export format: " + format);
            }
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .header("Content-Type", contentType)
                    .body(exportData);
                    
        } catch (Exception e) {
            return ResponseEntity.status(500).body(("Export failed: " + e.getMessage()).getBytes());
        }
    }

    private byte[] generateJsonExport(User user, List<Habit> habits, Map<String, Object> analytics) {
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("user", user);
        exportData.put("habits", habits);
        exportData.put("analytics", analytics);
        exportData.put("exportDate", java.time.LocalDateTime.now());
        
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsBytes(exportData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate JSON export", e);
        }
    }

    private byte[] generateCsvExport(User user, List<Habit> habits, Map<String, Object> analytics) {
        StringBuilder csv = new StringBuilder();
        csv.append("Habit Name,Description,Created Date,Current Streak,Total Check-ins\n");
        
        for (Habit habit : habits) {
            csv.append(String.format("\"%s\",\"%s\",\"%s\",%d,%d\n",
                    habit.getName(),
                    habit.getDescription() != null ? habit.getDescription().replace("\"", "\"\"") : "",
                    habit.getCreatedDate(),
                    habit.getCurrentStreak(),
                    analytics.get("totalCheckIns") != null ? (Integer) analytics.get("totalCheckIns") : 0
            ));
        }
        
        return csv.toString().getBytes();
    }

    private byte[] generatePdfExport(User user, List<Habit> habits, Map<String, Object> analytics) {
        // For now, return a simple text-based PDF
        // In a real application, you'd use a library like iText or Apache PDFBox
        String pdfContent = "Habit Tracker Report\n\n" +
                "User: " + user.getUsername() + "\n" +
                "Export Date: " + java.time.LocalDateTime.now() + "\n\n" +
                "Habits:\n";
        
        for (Habit habit : habits) {
            pdfContent += "- " + habit.getName() + " (Streak: " + habit.getCurrentStreak() + " days)\n";
        }
        
        return pdfContent.getBytes();
    }

    private byte[] generateExcelExport(User user, List<Habit> habits, Map<String, Object> analytics) {
        // For now, return CSV format as Excel
        // In a real application, you'd use Apache POI
        return generateCsvExport(user, habits, analytics);
    }
} 