package com.university.attendance.services;

import com.university.attendance.entities.Course;

import javax.persistence.*;
import java.util.List;

public class CourseService {

    private EntityManager em;

    public CourseService(EntityManager em) {
        this.em = em;
    }

    public void addCourse(String name) {
        em.getTransaction().begin();
        Course c = new Course();
        c.setName(name);
        em.persist(c);
        em.getTransaction().commit();
    }

    public Course getCourse(Long id) {
        return em.find(Course.class, id);
    }

    public List<Course> getAllCourses() {
        return em.createQuery("SELECT c FROM Course c", Course.class).getResultList();
    }

    public void updateCourse(Long id, String newName) {
        em.getTransaction().begin();
        Course c = em.find(Course.class, id);
        if (c != null) {
            c.setName(newName);
            em.merge(c);
        }
        em.getTransaction().commit();
    }

    public void deleteCourse(Long id) {
        em.getTransaction().begin();
        Course c = em.find(Course.class, id);
        if (c != null) {
            em.remove(c);
        }
        em.getTransaction().commit();
    }
}
