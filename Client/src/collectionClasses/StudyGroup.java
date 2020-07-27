package collectionClasses;

import java.io.Serializable;
import java.time.LocalDateTime;

public class StudyGroup implements Comparable<StudyGroup>, Serializable {
    private static final long serialVersionUID = 42L;
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Integer studentsCount; //Значение поля должно быть больше 0, Поле может быть null
    private FormOfEducation formOfEducation; //Поле может быть null
    private Semester semesterEnum; //Поле может быть null
    private Person groupAdmin; //Поле может быть null
    private String login;

    public StudyGroup(long id, String name, Coordinates coordinates, Integer studentsCount, FormOfEducation formOfEducation, Semester semesterEnum, Person groupAdmin, String login) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.studentsCount = studentsCount;
        this.formOfEducation = formOfEducation;
        this.semesterEnum = semesterEnum;
        this.groupAdmin = groupAdmin;
        this.creationDate = LocalDateTime.now();
        this.login = login;
    }

    public LocalDateTime getCreationDate() {
        return LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Integer getStudentsCount() {
        return studentsCount;
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public Semester getSemesterEnum() {
        return semesterEnum;
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public int compareTo(StudyGroup o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return "[" +
                "id = " + id +
                ", name = '" + name + '\'' +
                ", coordinates = " + coordinates +
                ", creationDate = " + creationDate +
                ", studentsCount = " + studentsCount +
                ", formOfEducation = " + formOfEducation +
                ", semesterEnum = " + semesterEnum +
                ", groupAdmin = " + groupAdmin +
                ", login = " + login +
                ']' + "\n";
    }
}