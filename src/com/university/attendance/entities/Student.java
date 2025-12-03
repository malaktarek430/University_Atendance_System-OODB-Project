package com.university.attendance.entities;

import javax.persistence.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;
import com.university.attendance.entities.Attendance;

@Entity
@NamedQuery(name="Student.findByName", query="SELECT s FROM Student s WHERE s.name = :name")
public class Student {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private String email;

    @OneToMany(mappedBy="student", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Attendance> attendances = new ArrayList<>();

    public Student() {}

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public List<Attendance> getAttendances() { return attendances; }
}

