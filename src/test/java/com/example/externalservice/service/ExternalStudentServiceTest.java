package com.example.externalservice.service;

import com.example.externalservice.domain.ExternalStudent;
import com.example.externalservice.dto.ExternalStudentRequest;
import com.example.externalservice.dto.ExternalStudentResponse;
import com.example.externalservice.mapper.ExternalStudentMapper;
import com.example.externalservice.repository.ExternalStudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalStudentServiceTest {

    @Mock
    ExternalStudentRepository externalStudentRepository;

    @Mock
    ExternalStudentMapper externalStudentMapper;

    @InjectMocks
    ExternalStudentService externalStudentService;

    @Test
    void create_shouldReturnResponseWithGeneratedExtraInfo() {
        UUID studentId = UUID.randomUUID();

        ExternalStudentRequest request = new ExternalStudentRequest(
                studentId,
                "Bob",
                "bob@mail.com",
                18
        );

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");

        ExternalStudent savedStudent = new ExternalStudent();
        savedStudent.setStudentId(studentId);
        savedStudent.setExtraInfo("extra-info-for-Bob");

        ExternalStudentResponse response =
                new ExternalStudentResponse(studentId, "extra-info-for-Bob");

        when(externalStudentMapper.toEntity(request)).thenReturn(externalStudent);
        when(externalStudentRepository.save(externalStudent)).thenReturn(savedStudent);
        when(externalStudentMapper.toResponse(savedStudent)).thenReturn(response);

        ExternalStudentResponse result = externalStudentService.create(request);

        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());

        verify(externalStudentMapper).toEntity(request);
        verify(externalStudentRepository).save(externalStudent);
        verify(externalStudentMapper).toResponse(savedStudent);
    }

    @Test
    void getByStudentId_shouldReturnResponse_whenStudentExists() {
        UUID studentId = UUID.randomUUID();

        ExternalStudent externalStudent = new ExternalStudent();
        externalStudent.setStudentId(studentId);
        externalStudent.setExtraInfo("extra-info-for-Bob");

        ExternalStudentResponse response =
                new ExternalStudentResponse(studentId, "extra-info-for-Bob");

        when(externalStudentRepository.findByStudentId(studentId))
                .thenReturn(Optional.of(externalStudent));
        when(externalStudentMapper.toResponse(externalStudent))
                .thenReturn(response);

        ExternalStudentResponse result = externalStudentService.getByStudentId(studentId);

        assertEquals(studentId, result.getStudentId());
        assertEquals("extra-info-for-Bob", result.getExtraInfo());

        verify(externalStudentRepository).findByStudentId(studentId);
        verify(externalStudentMapper).toResponse(externalStudent);
    }

    @Test
    void getByStudentId_shouldThrowException_whenStudentNotFound() {
        UUID studentId = UUID.randomUUID();

        when(externalStudentRepository.findByStudentId(studentId))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> externalStudentService.getByStudentId(studentId));

        verify(externalStudentRepository).findByStudentId(studentId);
    }

    @Test
    void deleteByStudentId_shouldCallRepository() {
        UUID studentId = UUID.randomUUID();

        externalStudentService.deleteByStudentId(studentId);

        verify(externalStudentRepository).deleteByStudentId(studentId);
    }
}