package commands;

import collectionClasses.StudyGroup;

import java.util.Comparator;

/**
 * Класс компаратор сортирующий form of education
 */
public class ComparatorByFormOfEducation implements Comparator<StudyGroup> {
    @Override
    public int compare(StudyGroup o1, StudyGroup o2) {
        return o2.getFormOfEducation().compareTo(o1.getFormOfEducation());
    }
}