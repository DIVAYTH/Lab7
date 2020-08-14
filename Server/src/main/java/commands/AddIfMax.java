package commands;

import collectionClasses.StudyGroup;
import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

public class AddIfMax extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public AddIfMax(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод добавляет элемент в коллекцию, если его height больше максимального
     *
     * @param studyGroup
     * @param login
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key, StudyGroup studyGroup, String login) throws InterruptedException {
        Runnable addElement = () -> {
            if (!(manager.col.size() == 0)) {
                Stream<StudyGroup> stream = manager.col.stream();
                Integer heightMAX = stream.filter(col -> col.getGroupAdmin().getHeight() != null)
                        .max(Comparator.comparingInt(p -> p.getGroupAdmin().getHeight())).get().getGroupAdmin().getHeight();
                if (studyGroup.getGroupAdmin().getHeight() != null && studyGroup.getGroupAdmin().getHeight() > heightMAX) {
                    try {
                        long id = bdActivity.getSQLId();
                        bdActivity.addToSQL(studyGroup, login, id);
                        studyGroup.setId(id);
                        studyGroup.setLogin(login);
                        manager.col.add(studyGroup);
                        poolSend.submit(new ServerSender(key, "Элемент коллекции добавлен"));
                    } catch (SQLException e) {
                        poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
                    } catch (NullPointerException e) {
                        poolSend.submit(new ServerSender(key, "Данные в скрипте введены не верно"));
                    }
                } else {
                    poolSend.submit(new ServerSender(key, "Элемент коллекции не сохранен, так как его height меньше " +
                            "height других элементов коллекции или равен null"));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(addElement).start();
    }
}