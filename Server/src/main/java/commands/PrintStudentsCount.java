package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintStudentsCount extends AbstractCommand {

    /**
     * Метод выводит students count в порядке возрастания
     *
     * @return
     */
    @Override
    public String execute() {
        if (!(CollectionManager.getManager().col.size() == 0)) {
            Stream<StudyGroup> stream = CollectionManager.getManager().col.stream();
            return stream.filter(col -> col.getStudentsCount() != null).sorted(new ComparatorByStudentCount())
                    .map(col -> "students count" + " - " + col.getStudentsCount()).collect(Collectors.joining("\n"));
        }
        return "Коллекция пустая";
    }
}