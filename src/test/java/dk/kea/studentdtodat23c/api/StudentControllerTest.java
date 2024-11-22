package dk.kea.studentdtodat23c.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.kea.studentdtodat23c.dto.StudentRequestDTO;
import dk.kea.studentdtodat23c.dto.StudentResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Optional: Use a separate test profile
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For serializing/deserializing JSON

    @Test
    void shouldGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetStudentById() throws Exception {
        // Given: A student exists in the database
        StudentRequestDTO request = new StudentRequestDTO(
                "John Doe",
                "password123",
                LocalDate.of(2000, 1, 15),
                LocalTime.of(10, 30)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        Long studentId = objectMapper.readValue(result.getResponse().getContentAsString(), StudentResponseDTO.class).id();

        // When: We fetch the student by ID
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.bornDate").value("2000-01-15"))
                .andExpect(jsonPath("$.bornTime").value("10:30:00"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentStudent() throws Exception {
        mockMvc.perform(get("/api/students/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateStudent() throws Exception {
        // Given: A new student request
        StudentRequestDTO request = new StudentRequestDTO(
                "Jane Smith",
                "securePass",
                LocalDate.of(1995, 5, 20),
                LocalTime.of(14, 45)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        // When: We send a POST request
        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Smith"))
                .andExpect(jsonPath("$.bornDate").value("1995-05-20"))
                .andExpect(jsonPath("$.bornTime").value("14:45:00"));
    }

    @Test
    void shouldUpdateStudent() throws Exception {
        // Given: A student exists in the database
        StudentRequestDTO createRequest = new StudentRequestDTO(
                "John Doe",
                "password123",
                LocalDate.of(2001, 3, 10),
                LocalTime.of(9, 15)
        );
        String createJson = objectMapper.writeValueAsString(createRequest);

        MvcResult createResult = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andReturn();

        Long studentId = objectMapper.readValue(createResult.getResponse().getContentAsString(), StudentResponseDTO.class).id();

        // When: We send an update request
        StudentRequestDTO updateRequest = new StudentRequestDTO(
                "John Updated",
                "newPassword",
                LocalDate.of(2001, 3, 10),
                LocalTime.of(9, 30)
        );
        String updateJson = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/api/students/{id}", studentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.bornDate").value("2001-03-10"))
                .andExpect(jsonPath("$.bornTime").value("09:30:00"));
    }

    @Test
    void shouldDeleteStudent() throws Exception {
        // Given: A student exists in the database
        StudentRequestDTO request = new StudentRequestDTO(
                "Jane Doe",
                "password123",
                LocalDate.of(1999, 8, 25),
                LocalTime.of(16, 0)
        );
        String jsonRequest = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andReturn();

        Long studentId = objectMapper.readValue(result.getResponse().getContentAsString(), StudentResponseDTO.class).id();

        // When: We delete the student
        mockMvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNoContent());

        // Then: The student should not be found
        mockMvc.perform(get("/api/students/{id}", studentId))
                .andExpect(status().isNotFound());
    }
}