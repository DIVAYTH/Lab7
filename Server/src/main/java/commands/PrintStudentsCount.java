package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrintStudentsCount extends AbstractCommand {
    private CollectionManager manager;
    private String answer;

    public PrintStudentsCount(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Метод выводит students count в порядке возрастания
     *
     * @return
     */
    @Override
    public String execute() throws InterruptedException {
        Runnable print = () -> {
            synchronized (this) {
                if (!(manager.col.size() == 0)) {
                    Stream<StudyGroup> stream = manager.col.stream();
                    answer = stream.filter(col -> col.getStudentsCount() != null).sorted(new ComparatorByStudentCount())
                            .map(col -> "students count" + " - " + col.getStudentsCount()).collect(Collectors.joining("\n"));
                }
                answer = "Коллекция пустая";
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