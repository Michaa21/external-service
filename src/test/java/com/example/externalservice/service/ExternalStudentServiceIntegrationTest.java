package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.repository.ExternalStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
class ExternalStudentServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    ExternalStudentService externalStudentService;

    @Autowired
    ExternalStudentRepository externalStudentRepository;

    @BeforeEach
    void setUp() {
        externalStudentRepository.deleteAll();
    }

    @Test
    void create_shouldSaveExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudentRequest request = new ExternalStudentRequest(
                studentId,
                "Bob",
                "bob@mail.com",
                18
        );

        ExternalStudentResponse result = externalStudentService.create(request);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());

        ExternalStudent saved = externalStudentRepository.findByStudentId(studentId)
                .orElseThrow();

        assertEquals(studentId, saved.getStudentId());
        assertEquals("extra-info-for-Bob", saved.getExtraInfo());
    }

    @Test
    void getByStudentId_shouldReturnExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");
        externalStudent.setName("Bob");
        externalStudent.setEmail("bob@mail.com");
        externalStudent.setAge(18);

        externalStudentRepository.save(externalStudent);

        ExternalStudentResponse result = externalStudentService.getByStudentId(studentId);

        assertNotNull(result);
        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());
    }

    @Test
    void getByStudentId_shouldThrowException_whenStudentNotFound() {
        UUID studentId = UUID.randomUUID();

        assertThrows(EntityNotFoundException.class,
                () -> externalStudentService.getByStudentId(studentId));
    }

    @Test
    void deleteByStudentId_shouldDeleteExternalStudent() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");
        externalStudent.setName("Bob");
        externalStudent.setEmail("bob@mail.com");
        externalStudent.setAge(18);

        externalStudentRepository.save(externalStudent);

        externalStudentService.deleteByStudentId(studentId);

        assertTrue(externalStudentRepository.findByStudentId(studentId).isEmpty());
    }
}