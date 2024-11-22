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

    @Test
    void testFindById() {
        Student student = studentRepository.findById(1L).get();
        assertEquals("Anders", student.getName());
    }

    @Test
    void testSave() {
        Student student = new Student();
        student.setName("Anders");
        student.setPassword("password");
        student.setBornDate(LocalDate.of(2020, 1, 1));
        student.setBornTime(LocalTime.of(12, 00));
        Student savedStudent = studentRepository.save(student);
        assertEquals("Anders", savedStudent.getName());
    }

    @Test
    void testUpdate() {
        Student student = studentRepository.findById(1L).get();
        student.setName("Charlie");
        Student updatedStudent = studentRepository.save(student);
        assertEquals("Charlie", updatedStudent.getName());
    }

    @Test
    void testDelete() {
        studentRepository.deleteById(1L);
        //List<Student> students = studentRepository.findAll();
        //assertEquals(1, students.size());

        assertFalse(studentRepository.existsById(1L));

    }
}