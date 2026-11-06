
package com.example.testdrivesservice;

import com.example.testdrivesservice.model.TestDrive;
import com.example.testdrivesservice.repository.TestDriveRepository;
import com.example.testdrivesservice.repository.UserAccountRepository;
import com.example.testdrivesservice.service.TestDriveService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestDriveServiceTest {

    @Mock
    private TestDriveRepository testDriveRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private TestDriveService testDriveService;

    @Test
    void findAllReturnsList() {
        List<TestDrive> expected = List.of(new TestDrive());
        when(testDriveRepository.findAll()).thenReturn(expected);

        List<TestDrive> actual = testDriveService.findAll();

        assertEquals(expected, actual);
        verify(testDriveRepository).findAll();
    }

    @Test
    void findByIdReturnsExistingRecord() {
        TestDrive expected = new TestDrive();
        when(testDriveRepository.findById(1))
                .thenReturn(Optional.of(expected));

        TestDrive actual = testDriveService.findById(1);

        assertSame(expected, actual);
    }

    @Test
    void findByIdThrowsWhenRecordDoesNotExist() {
        when(testDriveRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                ResponseStatusException.class,
                () -> testDriveService.findById(999)
        );
    }
}
