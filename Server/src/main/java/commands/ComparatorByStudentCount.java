package commands;

import collectionClasses.StudyGroup;

import java.util.Comparator;

/**
 * Класс компаратор сортирующий students count
 */
public class ComparatorByStudentCount implements Comparator<StudyGroup> {
    @Override
    public int compare(StudyGroup o1, StudyGroup o2) {
        return o1.getStudentsCount() - o2.getStudentsCount();
    }
}