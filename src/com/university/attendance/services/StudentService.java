package com.university.attendance.services;

import com.university.attendance.entities.Student;

import javax.persistence.*;
import java.util.List;

public class StudentService {

    private EntityManager em;

    public StudentService(EntityManager em) {
        this.em = em;
    }

    public void addStudent(String name, String email) {
        em.getTransaction().begin();
        Student s = new Student(name, email);
        em.persist(s);
        em.getTransaction().commit();
    }

    public Student getStudent(Long id) {
        return em.find(Student.class, id);
    }

    public List<Student> getAllStudents() {
        return em.createQuery("SELECT s FROM Student s", Student.class).getResultList();
    }

    public void updateStudent(Long id, String newName, String newEmail) {
        em.getTransaction().begin();
        Student s = em.find(Student.class, id);
        if (s != null) {
            s.setName(newName);
            s.setEmail(newEmail);
            em.merge(s);
        }
        em.getTransaction().commit();
    }

    public void deleteStudent(Long id) {
        em.getTransaction().begin();
        Student s = em.find(Student.class, id);
        if (s != null) {
            em.remove(s);
        }
        em.getTransaction().commit();
    }
}
