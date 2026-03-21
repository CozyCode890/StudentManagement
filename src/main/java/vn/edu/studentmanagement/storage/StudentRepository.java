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

}