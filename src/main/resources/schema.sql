-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    age INTEGER,
    gender TEXT,
    email TEXT
);

-- 成绩表
CREATE TABLE IF NOT EXISTS score (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    student_id INTEGER NOT NULL,
    subject TEXT NOT NULL,
    score REAL,
    exam_date TEXT,
    FOREIGN KEY (student_id) REFERENCES student(id)
);

-- 插入测试数据
INSERT INTO student (name, age, gender, email) VALUES 
('张三', 20, '男', 'zhangsan@example.com'),
('李四', 21, '女', 'lisi@example.com'),
('王五', 19, '男', 'wangwu@example.com');

INSERT INTO score (student_id, subject, score, exam_date) VALUES 
(1, '数学', 85.5, '2024-01-15'),
(1, '英语', 92.0, '2024-01-16'),
(2, '数学', 78.0, '2024-01-15'),
(2, '英语', 88.5, '2024-01-16'),
(3, '数学', 95.0, '2024-01-15'),
(3, '英语', 76.0, '2024-01-16');
