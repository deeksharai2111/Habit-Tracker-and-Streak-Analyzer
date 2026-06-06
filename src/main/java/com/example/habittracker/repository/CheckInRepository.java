package com.example.habittracker.repository;

import com.example.habittracker.model.CheckIn;
import com.example.habittracker.model.Habit;
import com.example.habittracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    boolean existsByHabitAndCheckInDate(Habit habit, LocalDate checkInDate);
    List<CheckIn> findByHabitOrderByCheckInDateDesc(Habit habit);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.habit.user = :user")
    long countByHabitUser(@Param("user") User user);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.habit.user = :user AND c.checkInDate = :date")
    long countByHabitUserAndCheckInDate(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.habit.user = :user AND c.checkInDate >= :date")
    long countByHabitUserAndCheckInDateAfter(@Param("user") User user, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.habit = :habit")
    long countByHabit(@Param("habit") Habit habit);
    
    @Query("SELECT c FROM CheckIn c WHERE c.habit = :habit AND c.checkInDate BETWEEN :startDate AND :endDate ORDER BY c.checkInDate")
    List<CheckIn> findByHabitAndCheckInDateBetween(@Param("habit") Habit habit, 
                                                   @Param("startDate") LocalDate startDate, 
                                                   @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.habit = :habit AND c.checkInDate BETWEEN :startDate AND :endDate")
    long countByHabitAndCheckInDateBetween(@Param("habit") Habit habit, 
                                          @Param("startDate") LocalDate startDate, 
                                          @Param("endDate") LocalDate endDate);
}

