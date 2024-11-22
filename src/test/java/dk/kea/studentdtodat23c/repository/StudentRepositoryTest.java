package dk.kea.studentdtodat23c.repository;

import dk.kea.studentdtodat23c.model.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StudentRepositoryTest {
    @Autowired
    StudentRepository studentRepository;
/*
    @BeforeEach
    void init(){
        Student s1 = new Student(
                "Tim",
                "Secret",
                LocalDate.of(2023, 9, 26),
                LocalTime.of(13, 12, 55)
        );

        Student s2 = new Student(
                "Gordon",
                "The Beast",
                LocalDate.of(0, 12, 24),
                LocalTime.of(0, 0, 0)
        );
        studentRepository.save(s2);
        studentRepository.save(s1);
    }
*/
    @Test
    void testFindAll() {
        List<Student> students = studentRepository.findAll();
        assertEquals(2, students.size());
    }

}