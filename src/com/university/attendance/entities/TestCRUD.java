package com.university.attendance.entities;

import com.university.attendance.entities.*;
import com.university.attendance.services.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class TestCRUD {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("universityDB");
        EntityManager em = emf.createEntityManager();
     
     // -----------------------------
     em.getTransaction().begin();
     em.createQuery("DELETE FROM Attendance").executeUpdate();
     em.createQuery("DELETE FROM Student").executeUpdate();
     em.createQuery("DELETE FROM Course").executeUpdate();
     em.getTransaction().commit();
        // إنشاء Services
        StudentService studentService = new StudentService(em);
        CourseService courseService = new CourseService(em);
        AttendanceService attendanceService = new AttendanceService(em);

        // -----------------------------
       
        List<Student> existingStudents = studentService.getAllStudents();
        if (existingStudents == null) existingStudents = new ArrayList<>();

        boolean hasNehal = existingStudents.stream()
                .anyMatch(s -> "nehal@example.com".equals(s.getEmail()));
        if (!hasNehal) {
            studentService.addStudent("Nehal Muhsen", "nehal@example.com");
        }

        boolean hasMayar = existingStudents.stream()
                .anyMatch(s -> "mayar@example.com".equals(s.getEmail()));
        if (!hasMayar) {
            studentService.addStudent("Mayar Saeed", "mayar@example.com");
        }
        boolean hasMalak = existingStudents.stream()
                .anyMatch(s -> "malak@example.com".equals(s.getEmail()));
        if (!hasMalak) {
            studentService.addStudent("Malak Tarek", "malak@example.com");
        }

        // -----------------------------
        
        List<Course> existingCourses = courseService.getAllCourses();
        if (existingCourses == null) existingCourses = new ArrayList<>();

        boolean hasOOD = existingCourses.stream()
                .anyMatch(c -> "OOD".equals(c.getName()));
        if (!hasOOD) {
            courseService.addCourse("OOD");
        }

        boolean hasDB = existingCourses.stream()
                .anyMatch(c -> "Database ".equals(c.getName()));
        if (!hasDB) {
            courseService.addCourse("Database ");
        }

        // -----------------------------
        
        List<Student> studentsList = studentService.getAllStudents();
        List<Course> coursesList = courseService.getAllCourses();

        if (studentsList.size() >= 2 && coursesList.size() >= 2) {
            Student s1 = studentsList.get(0);
            Course c1 = coursesList.get(0);
            attendanceService.addAttendance(s1, c1, "Present");

            Student s2 = studentsList.get(1);
            Course c2 = coursesList.get(1);
            attendanceService.addAttendance(s2, c2, "Absent");
        }

        // -----------------------------
       
        System.out.println("All Students:");
        for (Student s : studentsList) {
            System.out.println(s.getId() + " - " + s.getName() + " - " + s.getEmail());
        }

        // -----------------------------
        
        if (!studentsList.isEmpty()) {
            Student firstStudent = studentsList.get(0);
            System.out.println("\nAttendance for Student: " + firstStudent.getName());
            List<Attendance> attendanceList = attendanceService.getAttendanceForStudent(firstStudent.getId());
            if (attendanceList != null) {
                for (Attendance a : attendanceList) {
                    String courseName = (a.getCourse() != null) ? a.getCourse().getName() : "Unknown Course";
                    System.out.println(courseName + " - " + a.getStatus());
                }
            }
        }

        em.close();
        emf.close();
    }
}