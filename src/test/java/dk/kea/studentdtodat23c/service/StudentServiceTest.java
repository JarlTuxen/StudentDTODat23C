package dk.kea.studentdtodat23c.service;

import dk.kea.studentdtodat23c.dto.StudentRequestDTO;
import dk.kea.studentdtodat23c.dto.StudentResponseDTO;
import dk.kea.studentdtodat23c.model.Student;
import dk.kea.studentdtodat23c.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;

@SpringBootTest
class StudentServiceTest {

    @Mock
    private StudentRepository mockedStudentRepository;

    StudentService studentService;


    @BeforeEach
    void init(){
        //Arrange Mock behaviors
        Student s1 = new Student();
        s1.setId(1L);
        s1.setName("Sigurd");
        s1.setPassword("Hemmeligt");
        s1.setBornDate(LocalDate.of(2010,11,12));
        s1.setBornTime(LocalTime.of(23, 59, 59));

        Student s2 = new Student();
        s2.setId(2L);
        s2.setName("Ida");
        s2.setPassword("Secret");
        s2.setBornDate(LocalDate.of(2000,1,1));
        s2.setBornTime(LocalTime.of(0, 0, 1));

        List<Student> studentList = new ArrayList<>();
        studentList.add(s1);
        studentList.add(s2);
        //findall giver liste af studerende
        Mockito.when(mockedStudentRepository.findAll()).thenReturn(studentList);
        //findById giver studerende på id=1 og empty på id=42
        Mockito.when(mockedStudentRepository.findById(1L)).thenReturn(Optional.of(s1));
        Mockito.when(mockedStudentRepository.findById(42L)).thenReturn(Optional.empty());
        //deleteById giver empty hvis id = 42 - doThrow bruges, da deleteById er void
        //Mockito.when(mockedStudentRepository.deleteById(42)).thenThrow(new StudentNotFoundException("Student not found with id: 42"));
        doThrow(new RuntimeException("Student not found with id: 42")).when(mockedStudentRepository).deleteById(42L);

        // Define the behavior using thenAnswer
        // The student passed in save, can be read from arguments in the InvocationOnMock object
        Mockito.when(mockedStudentRepository.save(ArgumentMatchers.any(Student.class))).thenAnswer(new Answer<Student>() {
            @Override
            public Student answer(InvocationOnMock invocation) throws Throwable {
                // Extract the student object passed as an argument to the save method
                Object[] arguments = invocation.getArguments();
                if (arguments.length > 0 && arguments[0] instanceof Student) {
                    Student studentToSave = (Student) arguments[0];
                    //er id = 0, så simuler create - er id sat så simuler opdater
                    if (studentToSave.getId()==null) {
                        //create - repository skal returnere studentobject med næste ledige id = 3
                        studentToSave.setId(3L);
                    }
                    return studentToSave;
                } else {
                    // Handle the case where the argument is not a Student (optional)
                    throw new IllegalArgumentException("Invalid argument type");
                }
            }
        });
        //inject mocked repository
        studentService = new StudentService(mockedStudentRepository);

    }

    @Test
    void getAllStudents() {
        //Arrange Mock is handled in @BeforeEach
        //Act
        List<StudentResponseDTO> studentDTOList = studentService.getAllStudents();
        //Assert
        assertEquals("Sigurd", studentDTOList.get(0).name());
        assertEquals("Ida", studentDTOList.get(1).name());
    }

    @Test
    void getStudentById() {
        //Arrange Mock is handled in @BeforeEach
        // Act
        StudentResponseDTO studentDTO = studentService.getStudentById(1L);
        //Assert
        assertEquals("Sigurd", studentDTO.name());
        //Act & Assert for non-existing student
        assertThrows(RuntimeException.class, () -> studentService.getStudentById(42L));
    }

    @Test
    void createStudent() {
        //Arrange Mock is handled in @BeforeEach
        //Arrange & Act
        StudentResponseDTO resultStudentDTO = studentService.createStudent(
                new StudentRequestDTO(
                        "Hugo",
                        "Secret",
                        LocalDate.of(2000,1,1),
                        LocalTime.of(0, 0, 1)
                ));
        //Assert
        assertEquals("Hugo", resultStudentDTO.name());
    }

    @Test
    void updateStudent() {
        //Arrange Mock is handled in @BeforeEach
        //Arrange
        StudentRequestDTO studentRequestDTO = new StudentRequestDTO(
                "Hugo",
                "Secret",
                        LocalDate.of(2000,1,1),
                        LocalTime.of(0, 0, 1));
        //Act
        StudentResponseDTO resultStudentDTO = studentService.updateStudent(1L,  studentRequestDTO);
        //Assert
        assertEquals(1, resultStudentDTO.id());
        assertEquals("Hugo", resultStudentDTO.name());
        //Act & Assert for non-existing student
        assertThrows(RuntimeException.class, () -> studentService.updateStudent(42L, studentRequestDTO));
    }

    @Test
    void deleteStudentById() {
        //Arrange Mock is handled in @BeforeEach
        //Act & Assert
        assertThrows(RuntimeException.class, () -> studentService.deleteStudent(42L));
    }
}