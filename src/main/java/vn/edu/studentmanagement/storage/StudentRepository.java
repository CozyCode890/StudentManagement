package vn.edu.studentmanagement.storage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import vn.edu.studentmanagement.model.Student;
public class StudentRepository {
    private List<Student> students = new ArrayList<>();
    public void add(Student s) {
        students.add(s);
    }
    public List<Student> findAll() {
        return new ArrayList<>(students);
    }

    public List<Student> findAllSortedById() {
    return students.stream()
            .sorted(Comparator.comparingInt(Student::getId))
            .collect(Collectors.toList());
}

    public void deleteById(int id) {
        students.removeIf(s -> s.getId() == id);
    }
   public List<Student> findAllSortedByLastName() {
    return students.stream()
            .sorted(Comparator.comparing(Student::getLastname, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());

    
}
public List<Student> findAllSortedByMajor() {
    return students.stream()
            .sorted(Comparator.comparing(Student::getMajor, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
}
public List<Student> findAllSortedByGender() {
    return students.stream()
            .sorted(Comparator.comparing(Student::getGender, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList());
}
}