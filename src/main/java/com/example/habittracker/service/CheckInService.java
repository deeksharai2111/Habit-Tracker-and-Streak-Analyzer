package com.example.habittracker.service;

import com.example.habittracker.model.CheckIn;
import com.example.habittracker.model.Habit;
import com.example.habittracker.repository.CheckInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CheckInService {

    @Autowired
    private CheckInRepository checkInRepository;

    @Autowired
    private HabitService habitService;

    @Transactional
    public CheckIn checkIn(Habit habit) {
        LocalDate today = LocalDate.now();
        
        // Check if already checked in today
        if (checkInRepository.existsByHabitAndCheckInDate(habit, today)) {
            throw new RuntimeException("Already checked in today for this habit");
        }

        CheckIn checkIn = new CheckIn(habit);
        checkIn = checkInRepository.save(checkIn);

        // Update streak
        updateHabitStreak(habit);

        return checkIn;
    }

    public boolean hasCheckedInToday(Habit habit) {
        return checkInRepository.existsByHabitAndCheckInDate(habit, LocalDate.now());
    }

    public List<CheckIn> getCheckInHistory(Habit habit) {
        return checkInRepository.findByHabitOrderByCheckInDateDesc(habit);
    }

    private void updateHabitStreak(Habit habit) {
        List<CheckIn> recentCheckIns = checkInRepository.findByHabitOrderByCheckInDateDesc(habit);
        
        int streak = 0;
        LocalDate currentDate = LocalDate.now();
        
        for (CheckIn checkIn : recentCheckIns) {
            if (checkIn.getCheckInDate().equals(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else if (checkIn.getCheckInDate().equals(currentDate)) {
                // Continue the streak
                streak++;
                currentDate = currentDate.minusDays(1);
            } else {
                // There's a gap in the streak
                break;
            }
        }
        
        habit.setCurrentStreak(streak);
        habitService.updateHabitStreak(habit);
    }
}

