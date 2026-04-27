package com.example.repository;

import com.example.entity.Student;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Student> findAll() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class));
    }

    public Student findById(Long id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        List<Student> students = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class), id);
        return students.isEmpty() ? null : students.get(0);
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            String sql = "INSERT INTO student (name, age, gender, email) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, student.getName(), student.getAge(), student.getGender(), student.getEmail());
            Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
            student.setId(id);
            return student;
        } else {
            String sql = "UPDATE student SET name = ?, age = ?, gender = ?, email = ? WHERE id = ?";
            jdbcTemplate.update(sql, student.getName(), student.getAge(), student.getGender(), student.getEmail(), student.getId());
            return student;
        }
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM student WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}
