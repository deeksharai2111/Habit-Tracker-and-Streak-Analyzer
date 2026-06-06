package com.example.habittracker.service;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private HabitService habitService;

    @Autowired
    private CheckInService checkInService;

    public void sendDailyReminder(User user) {
        List<Habit> habits = habitService.findByUser(user);
        
        if (habits.isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Hello ").append(user.getUsername()).append("!\n\n");
        message.append("Here are your habits for today:\n\n");

        int uncheckedCount = 0;
        for (Habit habit : habits) {
            boolean checkedInToday = checkInService.hasCheckedInToday(habit);
            String status = checkedInToday ? "✅ Done" : "⏳ Pending";
            message.append("• ").append(habit.getName()).append(" - ").append(status).append("\n");
            
            if (!checkedInToday) {
                uncheckedCount++;
            }
        }

        message.append("\n");
        if (uncheckedCount > 0) {
            message.append("You have ").append(uncheckedCount).append(" habit(s) left to complete today.\n");
            message.append("Keep up the great work!\n");
        } else {
            message.append("🎉 Congratulations! You've completed all your habits for today!\n");
        }

        message.append("\nBest regards,\nHabit Tracker Team");

        sendEmail(user.getUsername() + "@example.com", "Daily Habit Reminder", message.toString());
    }

    public void sendStreakMilestone(User user, Habit habit, int streakDays) {
        String subject = "Streak Milestone Achieved! 🔥";
        StringBuilder message = new StringBuilder();
        
        message.append("Congratulations ").append(user.getUsername()).append("!\n\n");
        message.append("You've achieved a ").append(streakDays).append("-day streak with your habit:\n");
        message.append("• ").append(habit.getName()).append("\n\n");
        
        if (streakDays >= 7) {
            message.append("🎉 You're building a strong foundation!\n");
        } else if (streakDays >= 30) {
            message.append("🌟 You're forming a lasting habit!\n");
        } else if (streakDays >= 100) {
            message.append("🏆 You're a habit master!\n");
        }
        
        message.append("\nKeep up the amazing work!\n");
        message.append("Best regards,\nHabit Tracker Team");

        sendEmail(user.getUsername() + "@example.com", subject, message.toString());
    }

    public void sendWeeklyReport(User user) {
        List<Habit> habits = habitService.findByUser(user);
        
        if (habits.isEmpty()) {
            return;
        }

        StringBuilder message = new StringBuilder();
        message.append("Weekly Habit Report for ").append(user.getUsername()).append("\n\n");
        message.append("Here's your progress this week:\n\n");

        int totalCheckIns = 0;
        int totalPossible = habits.size() * 7;

        for (Habit habit : habits) {
            // Count check-ins for the past week
            int weeklyCheckIns = countWeeklyCheckIns(habit);
            totalCheckIns += weeklyCheckIns;
            
            message.append("• ").append(habit.getName()).append("\n");
            message.append("  - Weekly check-ins: ").append(weeklyCheckIns).append("/7\n");
            message.append("  - Current streak: ").append(habit.getCurrentStreak()).append(" days\n\n");
        }

        double completionRate = totalPossible > 0 ? (double) totalCheckIns / totalPossible * 100 : 0;
        message.append("Overall completion rate: ").append(String.format("%.1f", completionRate)).append("%\n\n");

        if (completionRate >= 80) {
            message.append("🎉 Excellent work this week!\n");
        } else if (completionRate >= 60) {
            message.append("👍 Good progress! Keep it up!\n");
        } else {
            message.append("💪 Don't give up! Every day is a new opportunity.\n");
        }

        message.append("\nBest regards,\nHabit Tracker Team");

        sendEmail(user.getUsername() + "@example.com", "Weekly Habit Report", message.toString());
    }

    private int countWeeklyCheckIns(Habit habit) {
        // This is a simplified implementation
        // In a real application, you'd query the database for actual check-ins
        return (int) (Math.random() * 7); // Placeholder
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@habittracker.com");
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error but don't throw it to avoid breaking the main functionality
            System.err.println("Failed to send email: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(User user) {
        String subject = "Welcome to Habit Tracker! 🎉";
        StringBuilder message = new StringBuilder();
        
        message.append("Welcome ").append(user.getUsername()).append("!\n\n");
        message.append("Thank you for joining Habit Tracker. You're now ready to start building better habits!\n\n");
        message.append("Here are some tips to get started:\n");
        message.append("• Start with 1-3 simple habits\n");
        message.append("• Be consistent rather than perfect\n");
        message.append("• Track your progress daily\n");
        message.append("• Celebrate your streaks!\n\n");
        message.append("Ready to begin? Create your first habit now!\n\n");
        message.append("Best regards,\nHabit Tracker Team");

        sendEmail(user.getUsername() + "@example.com", subject, message.toString());
    }
} 