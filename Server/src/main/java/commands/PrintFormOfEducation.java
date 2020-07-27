package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintFormOfEducation extends AbstractCommand {

    /**
     * Метод выводит FormOfEducation в порядке убывания
     *
     * @return
     */
    @Override
    public String execute() {
        if (CollectionManager.getManager().col.size() != 0) {
            Stream<StudyGroup> stream = CollectionManager.getManager().col.stream();
            return stream.filter(col -> col.getFormOfEducation() != null).sorted(new ComparatorByFormOfEducation())
                    .map(col -> "formOfEducation" + " - " + col.getFormOfEducation()).collect(Collectors.joining("\n"));
        }
        return "Коллекция пустая";
    }
}