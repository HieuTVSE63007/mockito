package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.junit.jupiter.api.parallel.Execution;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock   private StudentRepository studentRepository;
    private StudentService underTest;


    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        //when
        underTest.getAllStudents();
        //then
        Mockito.verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        //given
        Student student = new Student(
                "Hieu",
                "hieutruong@gmail.com",
                Gender.MALE);
        //when
        underTest.addStudent(student);
        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);
        Mockito.verify(studentRepository).save(studentArgumentCaptor.capture());

        Student captureStudent = studentArgumentCaptor.getValue();
        assertThat(captureStudent).isEqualTo(student);
    }

    @Test
    void willThrowWhenEmailIsTaken() {
        //given
        Student student = new Student(
                "Hieu",
                "hieutruong@gmail.com",
                Gender.MALE);

        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");
        Mockito.verify(studentRepository, never()).save(any());
    }

    @Test
    void willThrowWhenIDIsNotExist() {
        //given
        given(studentRepository.existsById(anyLong())).willReturn(false);

        assertThatThrownBy(() -> underTest.deleteStudent(1L))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + 1L + " does not exists");

    }
    @Test
    void deleteStudent() {
        long id = 10;
        given(studentRepository.existsById(anyLong())).willReturn(true);
        //when
        underTest.deleteStudent(id);
        //then
        Mockito.verify(studentRepository).deleteById(id);

    }
}