package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Show extends AbstractCommand {

    /**
     * Метод выводит элементы
     *
     * @return
     */
    public String execute() {
        if (CollectionManager.getManager().col.size() != 0) {
            Stream<StudyGroup> stream = CollectionManager.getManager().col.stream();
            return stream.map(StudyGroup::toString).collect(Collectors.joining("\n"));
        }
        return "Коллекция пуста.";
    }
}