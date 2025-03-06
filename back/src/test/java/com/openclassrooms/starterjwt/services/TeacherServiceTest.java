package com.openclassrooms.starterjwt.services;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;

@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(TeacherServiceTest.class);

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        logger.info("Unit test: {}", testInfo.getDisplayName());
    }

    @ParameterizedTest(name = "Id {0} exist={1}")
    @CsvSource({ "1, true", "999, false" })
    @DisplayName("Find By ID")
    void findById_shouldReturnCorrectTeacher(Long id, boolean exists) {
        logger.info("Test findById avec ID={} (existe={})", id, exists);
        // GIVEN
        Teacher teacher = exists ? new Teacher(id, null, null, null, null) : null;
        when(teacherRepository.findById(id)).thenReturn(Optional.ofNullable(teacher));

        // WHEN
        Teacher result = teacherService.findById(id);

        // THEN
        verify(teacherRepository).findById(id);
        if (exists) {
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(id);
        } else {
            assertThat(result).isNull();
        }
        logger.info("Test terminé : ID={} → Résultat={}", id, result);
    }

    @Test
    @DisplayName("Find All")
    void findAll_shouldReturnListOfTeachers() {
        // GIVEN
        List<Teacher> teachers = List.of(new Teacher(1L, null, null, null, null),
                new Teacher(2L, null, null, null, null));
        when(teacherRepository.findAll()).thenReturn(teachers);

        // WHEN
        List<Teacher> result = teacherService.findAll();

        // THEN
        verify(teacherRepository).findAll();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Teacher::getId).containsExactly(1L, 2L);
    }

}
