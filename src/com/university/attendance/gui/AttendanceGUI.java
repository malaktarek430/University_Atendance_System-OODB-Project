package com.university.attendance.gui;

import com.university.attendance.entities.*;
import com.university.attendance.services.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.persistence.*;

public class AttendanceGUI extends JFrame {

    private EntityManagerFactory emf;
    private EntityManager em;

    private StudentService studentService;
    private CourseService courseService;
    private AttendanceService attendanceService;

    private JTable studentTable;
    private JTable courseTable;
    private JTable attendanceTable;

    private String[] studentColumns = {"ID", "Name", "Email"};
    private String[] courseColumns = {"ID", "Name"};
    private String[] attendanceColumns = {"Student", "Course", "Status"};

    private JComboBox<Student> studentCombo;
    private JComboBox<Course> courseCombo;
    private JComboBox<String> statusCombo;

    public AttendanceGUI() {
        
        emf = Persistence.createEntityManagerFactory("universityDB");
        em = emf.createEntityManager();

        studentService = new StudentService(em);
        courseService = new CourseService(em);
        attendanceService = new AttendanceService(em);

        
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Attendance").executeUpdate();
        em.createQuery("DELETE FROM Student").executeUpdate();
        em.createQuery("DELETE FROM Course").executeUpdate();
        em.getTransaction().commit();

        
        setTitle("University Attendance System");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JTabbedPane tabs = new JTabbedPane();

        JPanel studentPanel = createStudentPanel();
        JPanel coursePanel = createCoursePanel();
        JPanel attendancePanel = createAttendancePanel();

        tabs.addTab("Students", studentPanel);
        tabs.addTab("Courses", coursePanel);
        tabs.addTab("Attendance", attendancePanel);

        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }

    
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#f5f5f5"));

        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.decode("#f5f5f5"));
        JTextField studentSearchField = new JTextField(20);
        searchPanel.add(new JLabel("Search Student:"));
        searchPanel.add(studentSearchField);
        panel.add(searchPanel, BorderLayout.NORTH);

       
        studentTable = new JTable();
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(studentTable);
        panel.add(scroll, BorderLayout.CENTER);

        
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.setBackground(Color.decode("#f5f5f5"));

        JTextField nameField = new JTextField(12);
        JTextField emailField = new JTextField(12);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        styleButton(addButton, "#4CAF50");
        styleButton(updateButton, "#2196F3");
        styleButton(deleteButton, "#f44336");

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
form.add(emailField);
        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);

        panel.add(form, BorderLayout.SOUTH);

        
        addButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            if (!name.isEmpty() && !email.isEmpty()) {
                studentService.addStudent(name, email);
                refreshStudentTable();
                refreshStudentCombo();
                nameField.setText("");
                emailField.setText("");
            } else JOptionPane.showMessageDialog(this, "Please fill all fields");
        });

        updateButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                Long id = Long.parseLong(studentTable.getValueAt(selectedRow, 0).toString());
                String name = nameField.getText().trim();
                String email = emailField.getText().trim();
                if (!name.isEmpty() && !email.isEmpty()) {
                    studentService.updateStudent(id, name, email);
                    refreshStudentTable();
                    refreshStudentCombo();
                }
            } else JOptionPane.showMessageDialog(this, "Select a student to update");
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                Long id = Long.parseLong(studentTable.getValueAt(selectedRow, 0).toString());
                studentService.deleteStudent(id);
                refreshStudentTable();
                refreshStudentCombo();
            } else JOptionPane.showMessageDialog(this, "Select a student to delete");
        });

        
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = studentTable.getSelectedRow();
            if (selectedRow != -1) {
                nameField.setText(studentTable.getValueAt(selectedRow, 1).toString());
                emailField.setText(studentTable.getValueAt(selectedRow, 2).toString());
            }
        });

        
        studentSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void filter() {
                String text = studentSearchField.getText().trim().toLowerCase();
                List<Student> allStudents = studentService.getAllStudents();
                List<Student> filtered = allStudents.stream()
                        .filter(s -> s.getName().toLowerCase().contains(text) || s.getEmail().toLowerCase().contains(text))
                        .toList();
                String[][] data = new String[filtered.size()][3];
                for (int i = 0; i < filtered.size(); i++) {
                    Student s = filtered.get(i);
                    data[i][0] = String.valueOf(s.getId());
                    data[i][1] = s.getName();
                    data[i][2] = s.getEmail();
                }
                studentTable.setModel(new DefaultTableModel(data, studentColumns));
            }
        });

        refreshStudentTable();
        return panel;
    }

    private void refreshStudentTable() {
        List<Student> students = studentService.getAllStudents();
        String[][] data = new String[students.size()][3];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = String.valueOf(s.getId());
            data[i][1] = s.getName();
            data[i][2] = s.getEmail();
        }
        studentTable.
setModel(new DefaultTableModel(data, studentColumns));
    }

    private void refreshStudentCombo() {
        if (studentCombo == null) return;
        List<Student> students = studentService.getAllStudents();
        studentCombo.removeAllItems();
        for (Student s : students) studentCombo.addItem(s);
    }

    
    private JPanel createCoursePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#f5f5f5"));

        
        JPanel courseSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        courseSearchPanel.setBackground(Color.decode("#f5f5f5"));
        JTextField courseSearchField = new JTextField(20);
        courseSearchPanel.add(new JLabel("Search Course:"));
        courseSearchPanel.add(courseSearchField);
        panel.add(courseSearchPanel, BorderLayout.NORTH);

        
        courseTable = new JTable();
        courseTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseTable.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(courseTable);
        panel.add(scroll, BorderLayout.CENTER);

        
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.setBackground(Color.decode("#f5f5f5"));

        JTextField courseField = new JTextField(15);
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");

        styleButton(addButton, "#4CAF50");
        styleButton(updateButton, "#2196F3");
        styleButton(deleteButton, "#f44336");

        form.add(new JLabel("Course Name:"));
        form.add(courseField);
        form.add(addButton);
        form.add(updateButton);
        form.add(deleteButton);

        panel.add(form, BorderLayout.SOUTH);

        
        addButton.addActionListener(e -> {
            String name = courseField.getText().trim();
            if (!name.isEmpty()) {
                courseService.addCourse(name);
                courseField.setText("");
                refreshCourseTable();
                refreshCourseCombo();
            } else JOptionPane.showMessageDialog(this, "Enter course name");
        });

        updateButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                Long id = Long.parseLong(courseTable.getValueAt(selectedRow, 0).toString());
                String name = courseField.getText().trim();
                if (!name.isEmpty()) {
                    courseService.updateCourse(id, name);
                    refreshCourseTable();
                    refreshCourseCombo();
                }
            } else JOptionPane.showMessageDialog(this, "Select a course to update");
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                Long id = Long.parseLong(courseTable.getValueAt(selectedRow, 0).toString());
                courseService.deleteCourse(id);
                refreshCourseTable();
                refreshCourseCombo();
            } else JOptionPane.showMessageDialog(this, "Select a course to delete");
        });

      
        courseTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = courseTable.getSelectedRow();
            if (selectedRow != -1) {
                courseField.setText(courseTable.getValueAt(selectedRow, 1).toString());
            }
        });

        
        courseSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.
event.DocumentEvent e) { filter(); }
            public void filter() {
                String text = courseSearchField.getText().trim().toLowerCase();
                List<Course> allCourses = courseService.getAllCourses();
                List<Course> filtered = allCourses.stream()
                        .filter(c -> c.getName().toLowerCase().contains(text))
                        .toList();
                String[][] data = new String[filtered.size()][2];
                for (int i = 0; i < filtered.size(); i++) {
                    Course c = filtered.get(i);
                    data[i][0] = String.valueOf(c.getId());
                    data[i][1] = c.getName();
                }
                courseTable.setModel(new DefaultTableModel(data, courseColumns));
            }
        });

        refreshCourseTable();
        return panel;
    }

    private void refreshCourseTable() {
        List<Course> courses = courseService.getAllCourses();
        String[][] data = new String[courses.size()][2];
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            data[i][0] = String.valueOf(c.getId());
            data[i][1] = c.getName();
        }
        courseTable.setModel(new DefaultTableModel(data, courseColumns));
    }

    private void refreshCourseCombo() {
        if (courseCombo == null) return;
        List<Course> courses = courseService.getAllCourses();
        courseCombo.removeAllItems();
        for (Course c : courses) courseCombo.addItem(c);
    }

    // ------------------- Attendance Panel -------------------
    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.decode("#f5f5f5"));

        attendanceTable = new JTable();
        attendanceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        attendanceTable.setRowHeight(25);
        JScrollPane scroll = new JScrollPane(attendanceTable);
        panel.add(scroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        form.setBackground(Color.decode("#f5f5f5"));

        studentCombo = new JComboBox<>();
        courseCombo = new JComboBox<>();
        statusCombo = new JComboBox<>(new String[]{"Present", "Absent"});
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");

        styleButton(addButton, "#4CAF50");
        styleButton(deleteButton, "#f44336");

        form.add(new JLabel("Student:"));
        form.add(studentCombo);
        form.add(new JLabel("Course:"));
        form.add(courseCombo);
        form.add(new JLabel("Status:"));
        form.add(statusCombo);
        form.add(addButton);
        form.add(deleteButton);

        addButton.addActionListener(e -> {
            Student s = (Student) studentCombo.getSelectedItem();
            Course c = (Course) courseCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            if (s != null && c != null) {
                attendanceService.addAttendance(s, c, status);
                refreshAttendanceTable();
            } else JOptionPane.showMessageDialog(this, "Select student and course");
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = attendanceTable.getSelectedRow();
            if (selectedRow != -1) {
                String studentName = attendanceTable.getValueAt(selectedRow, 0).toString();
                String courseName = attendanceTable.getValueAt(selectedRow, 1).toString();
                List<Attendance> list = attendanceService.getAttendanceForStudent(studentService.getAllStudents()
                        .stream().filter(s -> s.getName().equals(studentName)).findFirst().get().getId());
                for (Attendance a : list) {
                    if (a.getCourse().getName().equals(courseName)) {
                        em.getTransaction().begin();
                        em.remove(em.
contains(a) ? a : em.merge(a));
                        em.getTransaction().commit();
                        break;
                    }
                }
                refreshAttendanceTable();
            } else JOptionPane.showMessageDialog(this, "Select an attendance to delete");
        });

        panel.add(form, BorderLayout.SOUTH);
        refreshStudentCombo();
        refreshCourseCombo();
        refreshAttendanceTable();

        return panel;
    }

    private void refreshAttendanceTable() {
        List<Student> students = studentService.getAllStudents();
        List<Attendance> allAttendance = new java.util.ArrayList<>();
        for (Student s : students) {
            allAttendance.addAll(attendanceService.getAttendanceForStudent(s.getId()));
        }

        String[][] data = new String[allAttendance.size()][3];
        for (int i = 0; i < allAttendance.size(); i++) {
            Attendance a = allAttendance.get(i);
            data[i][0] = a.getStudent().getName();
            data[i][1] = a.getCourse().getName();
            data[i][2] = a.getStatus();
        }

        attendanceTable.setModel(new DefaultTableModel(data, attendanceColumns));
    }

    private void styleButton(JButton btn, String colorHex) {
        btn.setBackground(Color.decode(colorHex));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AttendanceGUI());
    }
}