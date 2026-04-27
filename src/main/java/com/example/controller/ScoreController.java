package com.example.controller;

import com.example.entity.Score;
import com.example.repository.ScoreRepository;
import com.example.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreController {

    private final ScoreRepository scoreRepository;
    private final StudentRepository studentRepository;

    public ScoreController(ScoreRepository scoreRepository, StudentRepository studentRepository) {
        this.scoreRepository = scoreRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Score> getScoreById(@PathVariable Long id) {
        Score score = scoreRepository.findById(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(score);
    }

    @GetMapping("/student/{studentId}")
    public List<Score> getScoresByStudentId(@PathVariable Long studentId) {
        return scoreRepository.findByStudentId(studentId);
    }

    @PostMapping
    public ResponseEntity<Score> createScore(@RequestBody Score score) {
        if (studentRepository.findById(score.getStudentId()) == null) {
            return ResponseEntity.badRequest().build();
        }
        Score createdScore = scoreRepository.save(score);
        return ResponseEntity.ok(createdScore);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Score> updateScore(@PathVariable Long id, @RequestBody Score scoreDetails) {
        Score score = scoreRepository.findById(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        if (studentRepository.findById(scoreDetails.getStudentId()) == null) {
            return ResponseEntity.badRequest().build();
        }
        score.setStudentId(scoreDetails.getStudentId());
        score.setSubject(scoreDetails.getSubject());
        score.setScore(scoreDetails.getScore());
        score.setExamDate(scoreDetails.getExamDate());
        Score updatedScore = scoreRepository.save(score);
        return ResponseEntity.ok(updatedScore);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScore(@PathVariable Long id) {
        Score score = scoreRepository.findById(id);
        if (score == null) {
            return ResponseEntity.notFound().build();
        }
        scoreRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
