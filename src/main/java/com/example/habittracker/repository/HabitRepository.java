package com.example.habittracker.repository;

import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUser(User user);
    List<Habit> findByUserOrderByCurrentStreakDesc(User user);
    
    @Query("SELECT h FROM Habit h WHERE h.id = :id AND h.user = :user")
    Optional<Habit> findByIdAndUser(@Param("id") Long id, @Param("user") User user);
}

