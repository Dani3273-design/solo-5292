# Spring Boot 3 + SQLite 示例项目

这是一个使用 Spring Boot 3 和 SQLite 数据库的示例项目，主要用于了解如何在 Spring Boot 中使用 SQLite。

## 技术栈

- **Spring Boot 3.2.5**
- **JDK 21**
- **Maven**
- **SQLite**
- **JdbcTemplate**

## 项目结构

```
main/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       │           ├── SqliteDemoApplication.java
│       │           ├── controller/
│       │           │   ├── StudentController.java
│       │           │   └── ScoreController.java
│       │           ├── entity/
│       │           │   ├── Student.java
│       │           │   └── Score.java
│       │           └── repository/
│       │               ├── StudentRepository.java
│       │               └── ScoreRepository.java
│       └── resources/
│           ├── application.properties
│           └── schema.sql
├── pom.xml
└── README.md
```

## 数据库设计

### 学生表 (student)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| name | TEXT | 姓名 |
| age | INTEGER | 年龄 |
| gender | TEXT | 性别 |
| email | TEXT | 邮箱 |

### 成绩表 (score)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键，自增 |
| student_id | INTEGER | 学生ID，外键 |
| subject | TEXT | 科目 |
| score | REAL | 分数 |
| exam_date | TEXT | 考试日期 |

## SQLite 使用说明

### 1. Maven 依赖配置

在 `pom.xml` 中需要添加以下依赖：

```xml
<!-- Spring Boot JDBC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- SQLite JDBC 驱动 -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.2.0</version>
</dependency>
```

### 2. 数据源配置

在 `application.properties` 中配置 SQLite 数据源：

```properties
# SQLite 驱动类
spring.datasource.driver-class-name=org.sqlite.JDBC

# 数据库文件路径（相对路径或绝对路径）
spring.datasource.url=jdbc:sqlite:./demo.db

# SQLite 不需要用户名密码
spring.datasource.username=
spring.datasource.password=
```

#### 重要说明：数据库文件的路径问题

##### 问题1：db文件会自动打包进jar/war文件吗？

**答案：不会，除非你将db文件放在特定目录。**

具体情况：

1. **当前配置（`./demo.db`）**：
   - 数据库文件位于**项目根目录**（运行时创建）
   - **不会**被Maven自动打包进jar/war文件
   - 每次部署到新环境时，会自动创建新的数据库文件

2. **如果将db文件放在`src/main/resources`目录下**：
   - 例如：`src/main/resources/demo.db`
   - Maven会将其**自动打包**进jar/war文件
   - 但问题是：jar/war是压缩包，SQLite**无法直接在压缩包内写入数据**
   - 结果：数据库会变成只读，所有写入操作会失败

3. **最佳实践**：
   - 不要将db文件打包进jar/war
   - 使用外部路径存储数据库文件
   - 通过环境变量或配置文件指定数据库路径

##### 问题2：相对路径是相对哪一级目录？

**答案：相对于 JVM 进程的工作目录（user.dir）。**

不同运行方式下的工作目录：

1. **使用 `mvn spring-boot:run` 运行**：
   ```
   工作目录 = 项目根目录（即 pom.xml 所在的目录）
   示例：/Users/yiigaa/work/solo-5292/main/
   所以 ./demo.db → /Users/yiigaa/work/solo-5292/main/demo.db
   ```

2. **使用 `java -jar xxx.jar` 运行**：
   ```
   工作目录 = 执行命令时的当前目录
   
   场景A：在jar所在目录执行
   cd /path/to/target/
   java -jar sqlite-demo-0.0.1-SNAPSHOT.jar
   → 工作目录 = /path/to/target/
   → ./demo.db → /path/to/target/demo.db
   
   场景B：在其他目录执行
   cd /home/user/
   java -jar /path/to/target/sqlite-demo-0.0.1-SNAPSHOT.jar
   → 工作目录 = /home/user/
   → ./demo.db → /home/user/demo.db
   ```

3. **部署到Tomcat等容器（war包）**：
   ```
   工作目录 = 取决于Tomcat的配置
   通常是 Tomcat 的 bin 目录 或 Catalina_BASE 目录
   
   例如：
   - Tomcat bin目录：/usr/local/tomcat/bin/
   - 所以 ./demo.db → /usr/local/tomcat/bin/demo.db
   ```

##### 推荐的配置方式

为了避免路径混乱，推荐以下方式：

**方式1：使用绝对路径（推荐用于生产环境）**
```properties
# Windows
spring.datasource.url=jdbc:sqlite:C:/data/demo.db

# Linux/Mac
spring.datasource.url=jdbc:sqlite:/var/data/demo.db
```

**方式2：使用环境变量**
```properties
# 通过环境变量指定路径
spring.datasource.url=jdbc:sqlite:${DB_PATH:./data/demo.db}
```

运行时指定：
```bash
export DB_PATH=/var/data/demo.db
java -jar sqlite-demo.jar
```

**方式3：使用Spring Boot的配置文件**
```properties
# application-dev.properties（开发环境）
spring.datasource.url=jdbc:sqlite:./demo.db

# application-prod.properties（生产环境）
spring.datasource.url=jdbc:sqlite:/var/data/prod.db
```

运行时指定环境：
```bash
java -jar sqlite-demo.jar --spring.profiles.active=prod
```

##### 路径配置总结表

| 运行方式 | 工作目录 | ./demo.db 实际位置 |
|---------|---------|-------------------|
| mvn spring-boot:run | 项目根目录 | `项目根目录/demo.db` |
| java -jar（jar所在目录） | jar所在目录 | `jar目录/demo.db` |
| java -jar（其他目录） | 当前目录 | `当前目录/demo.db` |
| Tomcat部署 | Tomcat bin目录 | `tomcat/bin/demo.db` |
| 绝对路径 | 不依赖 | 指定的绝对路径 |

### 3. 数据库初始化

使用 Spring Boot 的 SQL 初始化功能：

```properties
# 启用数据库初始化
spring.sql.init.mode=always

# 指定初始化脚本位置
spring.sql.init.schema-locations=classpath:schema.sql

# 遇到错误时继续（例如表已存在）
spring.sql.init.continue-on-error=true
```

### 4. 使用 JdbcTemplate

在 Repository 类中注入 `JdbcTemplate`：

```java
@Repository
public class StudentRepository {

    private final JdbcTemplate jdbcTemplate;

    public StudentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 查询所有
    public List<Student> findAll() {
        String sql = "SELECT * FROM student";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Student.class));
    }

    // 根据ID查询
    public Student findById(Long id) {
        String sql = "SELECT * FROM student WHERE id = ?";
        List<Student> students = jdbcTemplate.query(sql, 
            new BeanPropertyRowMapper<>(Student.class), id);
        return students.isEmpty() ? null : students.get(0);
    }

    // 插入数据并获取自增ID
    public Student save(Student student) {
        String sql = "INSERT INTO student (name, age, gender, email) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, student.getName());
            ps.setObject(2, student.getAge());
            ps.setString(3, student.getGender());
            ps.setString(4, student.getEmail());
            return ps;
        }, keyHolder);
        student.setId(keyHolder.getKey().longValue());
        return student;
    }
}
```

## API 接口

### 学生管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/students | 获取所有学生 |
| GET | /api/students/{id} | 根据ID获取学生 |
| POST | /api/students | 创建学生 |
| PUT | /api/students/{id} | 更新学生 |
| DELETE | /api/students/{id} | 删除学生（级联删除成绩） |

### 成绩管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/scores | 获取所有成绩 |
| GET | /api/scores/{id} | 根据ID获取成绩 |
| GET | /api/scores/student/{studentId} | 获取指定学生的所有成绩 |
| POST | /api/scores | 创建成绩 |
| PUT | /api/scores/{id} | 更新成绩 |
| DELETE | /api/scores/{id} | 删除成绩 |

## 运行项目

### 1. 编译项目

```bash
cd main
mvn clean package
```

### 2. 运行项目

```bash
mvn spring-boot:run
```

或运行打包后的 jar：

```bash
java -jar target/sqlite-demo-0.0.1-SNAPSHOT.jar
```

### 3. 访问接口

启动后访问：

- 学生列表：`http://localhost:8080/api/students`
- 成绩列表：`http://localhost:8080/api/scores`

## 测试示例

### 添加学生

```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{"name":"赵六","age":22,"gender":"男","email":"zhaoliu@example.com"}'
```

### 添加成绩

```bash
curl -X POST http://localhost:8080/api/scores \
  -H "Content-Type: application/json" \
  -d '{"studentId":1,"subject":"语文","score":88.5,"examDate":"2024-01-17"}'
```

### 查询学生的所有成绩

```bash
curl http://localhost:8080/api/scores/student/1
```

## SQLite 注意事项

1. **数据库文件**：SQLite 是文件型数据库，数据存储在指定的 `.db` 文件中。
2. **自增主键**：SQLite 使用 `INTEGER PRIMARY KEY AUTOINCREMENT` 定义自增主键。
3. **外键支持**：SQLite 默认关闭外键约束，如需启用需要在连接时设置：
   ```java
   jdbcTemplate.execute("PRAGMA foreign_keys = ON");
   ```
4. **数据类型**：SQLite 是动态类型的，但建议使用标准类型：
   - `INTEGER` - 整数
   - `REAL` - 浮点数
   - `TEXT` - 文本
   - `BLOB` - 二进制数据
5. **并发限制**：SQLite 不适合高并发写入场景，读取操作可以并发。

## 项目特点

- 使用 Spring Boot 3 最新版本
- 采用 JDK 21 的新特性
- 使用简洁的 JdbcTemplate 进行数据库操作
- SQLite 零配置，开箱即用
- 自动数据库初始化
- RESTful API 设计
- 级联删除支持
