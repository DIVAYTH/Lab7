package commands;

import collectionClasses.StudyGroup;
import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class Add extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public Add(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод добавляет элемент в коллекцию
     *
     * @param studyGroup
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key, StudyGroup studyGroup, String login) throws InterruptedException {
        Runnable addElement = () -> {
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
        };
        new Thread(addElement).start();
    }
}