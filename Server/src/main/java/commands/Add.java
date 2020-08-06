package commands;

import collectionClasses.StudyGroup;
import proga.BDActivity;
import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

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
    public String execute(StudyGroup studyGroup, String login) throws InterruptedException {
        Runnable addElement = () -> {
            synchronized (this) {
                try {
                    bdActivity.addToSQL(studyGroup, login);
                    studyGroup.setId(bdActivity.getMAXId());
                    studyGroup.setLogin(login);
                    manager.col.add(studyGroup);
                    answer = "Элемент коллекции добавлен";
                    notify();
                } catch (SQLException e) {
                    answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                }
            }
        };
        new Thread(addElement).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}