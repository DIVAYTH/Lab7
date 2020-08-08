package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

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
    public String execute() throws InterruptedException {
        Runnable print = () -> {
            synchronized (this) {
                if (manager.col.size() != 0) {
                    Stream<StudyGroup> stream = manager.col.stream();
                    answer = stream.filter(col -> col.getFormOfEducation() != null).sorted(new ComparatorByFormOfEducation())
                            .map(col -> "formOfEducation" + " - " + col.getFormOfEducation()).collect(Collectors.joining("\n"));
                } else {
                    answer = "Коллекция пустая";
                }
                notify();
            }
        };
        new Thread(print).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}