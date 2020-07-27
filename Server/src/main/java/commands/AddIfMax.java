package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.Comparator;
import java.util.stream.Stream;

public class AddIfMax extends AbstractCommand {
    Add add = new Add();

    /**
     * Метод добавляет элемент в коллекцию, если его height больше максимального
     *
     * @param studyGroup
     * @param login
     * @return
     */
    @Override
    public String execute(StudyGroup studyGroup, String login) throws InterruptedException {
        if (!(CollectionManager.getManager().col.size() == 0)) {
            Stream<StudyGroup> stream = CollectionManager.getManager().col.stream();
            Integer heightMAX = stream.filter(col -> col.getGroupAdmin().getHeight() != null)
                    .max(Comparator.comparingInt(p -> p.getGroupAdmin().getHeight())).get().getGroupAdmin().getHeight();
            if (studyGroup.getGroupAdmin().getHeight() != null && studyGroup.getGroupAdmin().getHeight() > heightMAX) {
                return add.execute(studyGroup, login);
            } else {
                return "Элемент коллекции не сохранен, так как его height меньше height других элементов коллекции или равен null";
            }
        } else {
            return "Коллекция пуста";
        }
    }
}