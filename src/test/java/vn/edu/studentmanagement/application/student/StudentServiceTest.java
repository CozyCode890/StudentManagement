package vn.edu.studentmanagement.application.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.edu.studentmanagement.domain.model.Gender;
import vn.edu.studentmanagement.domain.model.Student;
import vn.edu.studentmanagement.support.InMemoryRepository;

class StudentServiceTest {
  @Test
  void addStudentNormalizesBeforeValidation() {
    StudentService service = new StudentService(new InMemoryRepository<>());

    Student student = service.addStudent(" ititiu21001 ", "  nguyen   van   a ", "m");

    assertEquals("ITITIU21001", student.getId());
    assertEquals("Nguyen Van A", student.getFullName());
    assertEquals(Gender.MALE, student.getGender());
  }

  @Test
  void findByIdReturnsNullButFindRequiredByIdThrowsWhenStudentDoesNotExist() {
    StudentService service = new StudentService(new InMemoryRepository<>());

    assertNull(service.findById("ITITIU21001"));

    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> service.findRequiredById("ITITIU21001"));

    assertEquals("ID not found: ITITIU21001", exception.getMessage());
  }
}
