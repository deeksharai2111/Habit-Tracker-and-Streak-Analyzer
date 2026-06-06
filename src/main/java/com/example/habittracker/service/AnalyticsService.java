package com.example.habittracker.service;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.CheckInRepository;
import com.example.habittracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private CheckInRepository checkInRepository;

    public Map<String, Object> getUserAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();
        
        List<Habit> habits = habitRepository.findByUser(user);
        
        // Basic stats
        analytics.put("totalHabits", habits.size());
        analytics.put("activeHabits", habits.stream().filter(h -> h.getCurrentStreak() > 0).count());
        analytics.put("totalCheckIns", checkInRepository.countByHabitUser(user));
        
        // Streak analytics
        int bestStreak = habits.stream()
                .mapToInt(Habit::getCurrentStreak)
                .max()
                .orElse(0);
        analytics.put("bestStreak", bestStreak);
        
        // Completion rate for last 30 days
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        long totalPossibleCheckIns = habits.size() * 30;
        long actualCheckIns = checkInRepository.countByHabitUserAndCheckInDateAfter(user, thirtyDaysAgo);
        double completionRate = totalPossibleCheckIns > 0 ? (double) actualCheckIns / totalPossibleCheckIns * 100 : 0;
        analytics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
        
        // Weekly activity
        Map<String, Long> weeklyActivity = getWeeklyActivity(user);
        analytics.put("weeklyActivity", weeklyActivity);
        
        // Habit performance ranking
        List<Map<String, Object>> habitRanking = habits.stream()
                .map(habit -> {
                    Map<String, Object> habitData = new HashMap<>();
                    habitData.put("id", habit.getId());
                    habitData.put("name", habit.getName());
                    habitData.put("currentStreak", habit.getCurrentStreak());
                    habitData.put("totalCheckIns", checkInRepository.countByHabit(habit));
                    return habitData;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("currentStreak"), (Integer) a.get("currentStreak")))
                .collect(Collectors.toList());
        analytics.put("habitRanking", habitRanking);
        
        return analytics;
    }

    public Map<String, Object> getHabitAnalytics(Long habitId, User user, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analytics = new HashMap<>();
        
        Habit habit = habitRepository.findByIdAndUser(habitId, user)
                .orElseThrow(() -> new RuntimeException("Habit not found"));
        
        // Basic habit info
        analytics.put("habit", habit);
        
        // Check-in data for the date range
        List<LocalDate> checkInDates = checkInRepository.findByHabitAndCheckInDateBetween(habit, startDate, endDate)
                .stream()
                .map(checkIn -> checkIn.getCheckInDate())
                .collect(Collectors.toList());
        
        analytics.put("checkInDates", checkInDates);
        analytics.put("totalCheckIns", checkInDates.size());
        
        // Streak analysis
        Map<String, Object> streakAnalysis = analyzeStreaks(checkInDates, startDate, endDate);
        analytics.put("streakAnalysis", streakAnalysis);
        
        // Daily completion rate
        Map<String, Double> dailyCompletion = calculateDailyCompletion(checkInDates, startDate, endDate);
        analytics.put("dailyCompletion", dailyCompletion);
        
        // Monthly trends
        Map<String, Long> monthlyTrends = getMonthlyTrends(habit, startDate, endDate);
        analytics.put("monthlyTrends", monthlyTrends);
        
        return analytics;
    }

    private Map<String, Long> getWeeklyActivity(User user) {
        Map<String, Long> weeklyActivity = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long checkIns = checkInRepository.countByHabitUserAndCheckInDate(user, date);
            weeklyActivity.put(date.format(formatter), checkIns);
        }
        
        return weeklyActivity;
    }

    private Map<String, Object> analyzeStreaks(List<LocalDate> checkInDates, LocalDate startDate, LocalDate endDate) {
        Map<String, Object> analysis = new HashMap<>();
        
        if (checkInDates.isEmpty()) {
            analysis.put("currentStreak", 0);
            analysis.put("longestStreak", 0);
            analysis.put("averageStreak", 0.0);
            return analysis;
        }
        
        // Sort dates
        Collections.sort(checkInDates);
        
        int currentStreak = 0;
        int longestStreak = 0;
        List<Integer> allStreaks = new ArrayList<>();
        int tempStreak = 0;
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (checkInDates.contains(currentDate)) {
                tempStreak++;
                if (currentDate.equals(LocalDate.now())) {
                    currentStreak = tempStreak;
                }
            } else {
                if (tempStreak > 0) {
                    allStreaks.add(tempStreak);
                    longestStreak = Math.max(longestStreak, tempStreak);
                    tempStreak = 0;
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        
        // Handle case where streak continues to end date
        if (tempStreak > 0) {
            allStreaks.add(tempStreak);
            longestStreak = Math.max(longestStreak, tempStreak);
        }
        
        double averageStreak = allStreaks.isEmpty() ? 0.0 : 
                allStreaks.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        
        analysis.put("currentStreak", currentStreak);
        analysis.put("longestStreak", longestStreak);
        analysis.put("averageStreak", Math.round(averageStreak * 100.0) / 100.0);
        analysis.put("totalStreaks", allStreaks.size());
        
        return analysis;
    }

    private Map<String, Double> calculateDailyCompletion(List<LocalDate> checkInDates, LocalDate startDate, LocalDate endDate) {
        Map<String, Double> completion = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");
        
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            double rate = checkInDates.contains(currentDate) ? 100.0 : 0.0;
            completion.put(currentDate.format(formatter), rate);
            currentDate = currentDate.plusDays(1);
        }
        
        return completion;
    }

    private Map<String, Long> getMonthlyTrends(Habit habit, LocalDate startDate, LocalDate endDate) {
        Map<String, Long> trends = new LinkedHashMap<>();
        
        LocalDate currentDate = startDate.withDayOfMonth(1);
        while (!currentDate.isAfter(endDate)) {
            LocalDate monthEnd = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
            if (monthEnd.isAfter(endDate)) {
                monthEnd = endDate;
            }
            
            long checkIns = checkInRepository.countByHabitAndCheckInDateBetween(habit, currentDate, monthEnd);
            trends.put(currentDate.format(DateTimeFormatter.ofPattern("MMM yyyy")), checkIns);
            
            currentDate = currentDate.plusMonths(1);
        }
        
        return trends;
    }
} 