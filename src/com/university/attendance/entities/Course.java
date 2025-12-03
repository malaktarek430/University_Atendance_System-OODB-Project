package com.university.attendance.entities;

import javax.persistence.*;

@Entity
public class Course {

    @Id @GeneratedValue
    private Long id;

    private String name;

    public Course() {}

    public Course(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
