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
    public void executeCommand(ExecutorService poolSend, SelectionKey key, StudyGroup studyGroup, String login) throws InterruptedException {
        Runnable addElement = () -> {
            if (!(manager.col.size() == 0)) {
                Stream<StudyGroup> stream = manager.col.stream();
                Integer heightMIN = stream.filter(col -> col.getGroupAdmin().getHeight() != null)
                        .min(Comparator.comparingInt(p -> p.getGroupAdmin().getHeight())).get().getGroupAdmin().getHeight();
                if (studyGroup.getGroupAdmin().getHeight() != null && studyGroup.getGroupAdmin().getHeight() < heightMIN) {
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
                    poolSend.submit(new ServerSender(key, "Элемент коллекции не сохранен, так как его height больше height других элементов коллекции или равен null"));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(addElement).start();
    }
}