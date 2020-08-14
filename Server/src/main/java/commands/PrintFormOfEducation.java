package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintFormOfEducation extends AbstractCommand {
    private CollectionManager manager;
    private String answer;

    public PrintFormOfEducation(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Метод выводит FormOfEducation в порядке убывания
     *
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key) throws InterruptedException {
        Runnable print = () -> {
            if (manager.col.size() != 0) {
                Stream<StudyGroup> stream = manager.col.stream();
                poolSend.submit(new ServerSender(key, stream.filter(col -> col.getFormOfEducation() != null).sorted(new ComparatorByFormOfEducation())
                        .map(col -> "formOfEducation" + " - " + col.getFormOfEducation()).collect(Collectors.joining("\n"))));
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пустая"));
            }
        };
        new Thread(print).start();
    }
}