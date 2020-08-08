package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Show extends AbstractCommand {
    private CollectionManager manager;
    private String answer;

    public Show(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Метод выводит элементы коллекции
     *
     * @return
     */
    public String execute() throws InterruptedException {
        Runnable show = () -> {
            synchronized (this) {
                if (manager.col.size() != 0) {
                    Stream<StudyGroup> stream = manager.col.stream();
                    answer = stream.map(StudyGroup::toString).collect(Collectors.joining("\n"));
                } else {
                    answer = "Коллекция пуста.";
                }
                notify();
            }
        };
        new Thread(show).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}