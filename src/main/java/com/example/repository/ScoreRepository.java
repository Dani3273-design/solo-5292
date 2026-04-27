package com.example.repository;

import com.example.entity.Score;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ScoreRepository {

    private final JdbcTemplate jdbcTemplate;

    public ScoreRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Score> findAll() {
        String sql = "SELECT * FROM score";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Score.class));
    }

    public Score findById(Long id) {
        String sql = "SELECT * FROM score WHERE id = ?";
        List<Score> scores = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Score.class), id);
        return scores.isEmpty() ? null : scores.get(0);
    }

    public List<Score> findByStudentId(Long studentId) {
        String sql = "SELECT * FROM score WHERE student_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Score.class), studentId);
    }

    public Score save(Score score) {
        if (score.getId() == null) {
            String sql = "INSERT INTO score (student_id, subject, score, exam_date) VALUES (?, ?, ?, ?)";
            jdbcTemplate.update(sql, score.getStudentId(), score.getSubject(), score.getScore(), score.getExamDate());
            Long id = jdbcTemplate.queryForObject("SELECT last_insert_rowid()", Long.class);
            score.setId(id);
            return score;
        } else {
            String sql = "UPDATE score SET student_id = ?, subject = ?, score = ?, exam_date = ? WHERE id = ?";
            jdbcTemplate.update(sql, score.getStudentId(), score.getSubject(), score.getScore(), score.getExamDate(), score.getId());
            return score;
        }
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM score WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int deleteByStudentId(Long studentId) {
        String sql = "DELETE FROM score WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}
