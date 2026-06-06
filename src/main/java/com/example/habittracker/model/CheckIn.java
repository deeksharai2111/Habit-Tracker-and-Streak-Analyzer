package com.example.habittracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "check_ins", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"habit_id", "check_in_date"})
})
public class CheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate checkInDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id")
    private Habit habit;

    public CheckIn() {
        this.checkInDate = LocalDate.now();
    }

    public CheckIn(Habit habit) {
        this();
        this.habit = habit;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Habit getHabit() {
        return habit;
    }

    public void setHabit(Habit habit) {
        this.habit = habit;
    }
}

