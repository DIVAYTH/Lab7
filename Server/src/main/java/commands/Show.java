package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
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
    public void executeCommand(ExecutorService poolSend, SelectionKey key) throws InterruptedException {
        Runnable show = () -> {
            if (manager.col.size() != 0) {
                Stream<StudyGroup> stream = manager.col.stream();
                poolSend.submit(new ServerSender(key, stream.map(StudyGroup::toString).collect(Collectors.joining("\n"))));
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста."));
            }
        };
        new Thread(show).start();
    }
}