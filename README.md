# Student Management

A Java console application for managing students and course schedules. Data is stored locally in CSV files under the user's home directory, so previously entered students and schedules are preserved between runs.

## Features

- Student management:
  - View all students.
  - Search students by name.
  - Add a new student.
  - Delete a student by ID.
- Schedule management:
  - View a student's schedule.
  - Add a course to a schedule.
  - Remove a course from a schedule.
- Input validation:
  - Student IDs must follow the required format.
  - Student names may contain only letters and spaces.
  - Gender accepts `Male`, `Female`, `M`, or `F`.
  - Course IDs must exist in the course catalog.
  - Students can register for general courses or major-specific courses that match their major.
  - Each student can register for up to 3 courses.
  - Duplicate courses and time conflicts are not allowed.

## Tech Stack

- Java 21
- Maven
- JUnit 5
- Console application
- CSV file storage

## Requirements

Install:

- JDK 21 or later
- Maven 3.9 or later

Check your environment:

```bash
java -version
mvn -version
```

## Running the Application

From the project root, run:

```bash
mvn clean compile
mvn exec:java
```

Run tests:

```bash
mvn test
```

## Usage

After starting the application, the main menu is displayed:

```text
1) Manage students
2) Manage schedules
0) Quit
```

### Student Management

In the `Manage students` menu:

```text
1) View Student Menu (Search/List)
2) Add New Student
3) Delete Student (by ID)
0) Back to main menu
```

When adding a student, enter:

- Student ID
- Full name
- Gender

The student ID must contain exactly 11 characters:

```text
IT + major code + program code + year code + sequence number
```

Examples:

```text
ITITIU21001
ITDSWE22015
ITCSIU25199
```

Student ID rules:

- Must start with `IT`.
- Major code: `IT`, `CS`, or `DS`.
- Program code: `IU` or `WE`.
- Year code: from `21` to `25`.
- Sequence number: from `001` to `199`.

### Schedule Management

In the `Manage schedules` menu:

```text
1) View Student Schedule
2) Add Course to Schedule
3) Remove Course from Schedule
0) Back to main menu
```

When adding a course:

- The student must exist.
- The course must exist in the catalog.
- General courses are available to all majors.
- Major courses are available only to students in the matching major.
- Each student can register for up to 3 courses.
- Courses in the same schedule must not overlap in time.

## Course Catalog

### General Courses

| Course ID | Course Name | Time |
| --- | --- | --- |
| GEN101 | Calculus | Monday 08:00-09:30 |
| GEN102 | Physics | Tuesday 08:00-09:30 |
| GEN103 | Chemistry | Wednesday 08:00-09:30 |
| GEN104 | HCM Thought | Thursday 08:00-09:30 |

### IT Major

| Course ID | Course Name | Time |
| --- | --- | --- |
| IT201 | Programming Fundamentals | Monday 10:00-11:30 |
| IT202 | Database Systems | Wednesday 10:00-11:30 |
| IT203 | Data Structures | Friday 10:00-11:30 |

### CS Major

| Course ID | Course Name | Time |
| --- | --- | --- |
| CS201 | Algorithms | Monday 13:00-14:30 |
| CS202 | Software Engineering | Thursday 10:00-11:30 |
| CS203 | Computer Networks | Saturday 08:00-09:30 |

### DS Major

| Course ID | Course Name | Time |
| --- | --- | --- |
| DS201 | Machine Learning Basics | Tuesday 10:00-11:30 |
| DS202 | Statistics | Friday 13:00-14:30 |
| DS203 | Data Mining | Saturday 14:00-15:30 |

## Data Storage

The application automatically creates this directory:

```text
~/.student-manager
```

It stores data in 2 files:

```text
students.csv
schedules.csv
```

`students.csv` format:

```csv
student_id,full_name,major,gender
```

Example:

```csv
ITITIU21001,Nguyen Van A,IT,MALE
ITDSWE22015,Tran Thi B,DS,FEMALE
```

`schedules.csv` format:

```csv
student_id,course_id
```

Example:

```csv
ITITIU21001,GEN101
ITITIU21001,IT201
```

## Project Structure

```text
src/main/java/vn/edu/studentmanagement
├── application      # Application services and stores
├── bootstrap        # Application bootstrap and entry point
├── domain           # Models, validation, normalization, and course catalog
├── infrastructure   # CSV persistence
└── presentation     # Console UI, menus, controllers, and renderers
```

Entry point:

```text
vn.edu.studentmanagement.bootstrap.App
```

## Useful Maven Commands

```bash
mvn clean
mvn compile
mvn test
mvn exec:java
```

## Notes

- CSV data is stored in the user's home directory, not inside the repository.
- To reset all data, delete the files inside `~/.student-manager`.
- The application does not use a database; all persistence is handled through CSV files.
