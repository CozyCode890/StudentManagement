package vn.edu.studentmanagement.application.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import vn.edu.studentmanagement.support.InMemoryRepository;

class StudentServiceTest {
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
