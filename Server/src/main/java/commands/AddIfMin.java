package commands;

import collectionClasses.StudyGroup;
import proga.BDActivity;
import proga.CollectionManager;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

public class AddIfMin extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public AddIfMin(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод добавляет элемент в коллекцию, если его height меньше минимально
     *
     * @param studyGroup
     * @return
     */
    @Override
    public String execute(StudyGroup studyGroup, String login) throws InterruptedException, ExecutionException {
        Runnable addElement = () -> {
            synchronized (this) {
                if (!(manager.col.size() == 0)) {
                    Stream<StudyGroup> stream = manager.col.stream();
                    Integer heightMIN = stream.filter(col -> col.getGroupAdmin().getHeight() != null)
                            .min(Comparator.comparingInt(p -> p.getGroupAdmin().getHeight())).get().getGroupAdmin().getHeight();
                    if (studyGroup.getGroupAdmin().getHeight() != null && studyGroup.getGroupAdmin().getHeight() < heightMIN) {
                        try {
                            bdActivity.addToSQL(studyGroup, login);
                            studyGroup.setId(bdActivity.getMAXId());
                            studyGroup.setLogin(login);
                            manager.col.add(studyGroup);
                            answer = "Элемент коллекции добавлен";
                        } catch (SQLException e) {
                            answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                        }
                    } else {
                        answer = "Элемент коллекции не сохранен, так как его height больше height других элементов коллекции или равен null";
                    }
                } else {
                    answer = "Коллекция пуста";
                }
                notify();
            }
        };
        new Thread(addElement).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}