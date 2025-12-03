package com.university.attendance.services;

import com.university.attendance.entities.*;

import javax.persistence.*;
import java.util.List;

public class AttendanceService {

    private EntityManager em;

    public AttendanceService(EntityManager em) {
        this.em = em;
    }

    public void addAttendance(Student student, Course course, String status) {
        em.getTransaction().begin();
        Attendance a = new Attendance();
        a.setStudent(student);
        a.setCourse(course);
        a.setStatus(status);
        em.persist(a);
        em.getTransaction().commit();
    }

    public List<Attendance> getAttendanceForStudent(Long studentId) {
        return em.createQuery(
                "SELECT a FROM Attendance a WHERE a.student.id = :id",
                Attendance.class
        ).setParameter("id", studentId).getResultList();
    }

    public List<Attendance> getAttendanceForCourse(Long courseId) {
        return em.createQuery(
                "SELECT a FROM Attendance a WHERE a.course.id = :id",
                Attendance.class
        ).setParameter("id", courseId).getResultList();
    }
}