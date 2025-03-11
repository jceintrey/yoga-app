package com.openclassrooms.starterjwt.controllers;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.mapper.TeacherMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.services.TeacherService;

@ExtendWith(MockitoExtension.class)
public class TeacherControllerTest {

    private Teacher teacher1, teacher2;
    private TeacherDto teacher1Dto, teacher2Dto;
    private List<Teacher> teacherList;
    private List<TeacherDto> teacherDtoList;

    @Mock
    private TeacherService teacherService;

    @Mock
    private TeacherMapper teacherMapper;

    @InjectMocks
    private TeacherController teacherController;

    @BeforeEach
    void initEach() {
        teacher1 = new Teacher(1L, "Willis", "Bruce", null, null);
        teacher1Dto = new TeacherDto(1L, "willis", "Bruce", null, null);
        teacher2 = new Teacher(2L, "Carrey", "Jim", null, null);
        teacher2Dto = new TeacherDto(2L, "Carrey", "Jim", null, null);

        teacherList = new ArrayList<>();
        teacherList.add(teacher1);
        teacherList.add(teacher2);

        teacherDtoList = new ArrayList<>();
        teacherDtoList.add(teacher1Dto);
        teacherDtoList.add(teacher2Dto);

    }

    @ParameterizedTest(name = "Teacher exist {0}")
    @CsvSource({ "true", "false" })
    @Tag("get")
    void find_shouldReturnTeacherWhenExist(boolean exist) {
        // GIVEN
        Long teacherId = teacher1.getId();
        when(teacherService.findById(teacherId)).thenReturn(exist ? teacher1 : null);
        if (exist)
            when(teacherMapper.toDto(teacher1)).thenReturn(teacher1Dto);
        // WHEN
        ResponseEntity<?> result = teacherController.findById("" + teacher1.getId());

        // THEN
        if (exist) {
            verify(teacherService).findById(teacherId);
            assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(result.getBody()).isEqualTo(teacher1Dto);

        } else {
            verify(teacherService).findById(teacherId);
            assertThat(result.getStatusCodeValue()).isEqualTo(404);
        }
    }
    @Test
    @Tag("get")
    @DisplayName("Should return bad request when id is not valid ")
    void find_shouldReturnBadRequestWhenUserTeacherIdIsNotValid() {
        // GIVEN
        String invalidTeacherId = "invalidTeacherId";

        // WHEN
        ResponseEntity<?> result = teacherController.findById(invalidTeacherId);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @Tag("get")
    @DisplayName("Should get all teachers")
    void findAll_shouldReturnAllTeachers() throws Exception {
        // GIVEN
        when(teacherService.findAll()).thenReturn(teacherList);
        when(teacherMapper.toDto(teacherList)).thenReturn(teacherDtoList);

        // WHEN
        ResponseEntity<?> result = teacherController.findAll();

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(teacherDtoList);

    }
}
