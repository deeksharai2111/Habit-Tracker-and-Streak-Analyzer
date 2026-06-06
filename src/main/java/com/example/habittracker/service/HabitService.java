package com.example.habittracker.service;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import com.example.habittracker.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    public Habit save(Habit habit) {
        return habitRepository.save(habit);
    }

    public Optional<Habit> findById(Long id) {
        return habitRepository.findById(id);
    }

    public List<Habit> findByUser(User user) {
        return habitRepository.findByUser(user);
    }

    public void deleteById(Long id) {
        habitRepository.deleteById(id);
    }

    public void updateHabitStreak(Habit habit) {
        // This method will be called by CheckInService to update the streak
        habitRepository.save(habit);
    }

    public List<Habit> findByUserOrderByCurrentStreakDesc(User user) {
        return habitRepository.findByUserOrderByCurrentStreakDesc(user);
    }
}

