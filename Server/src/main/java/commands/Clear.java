package commands;

import proga.BDActivity;
import proga.CollectionManager;

import java.sql.SQLException;

public class Clear extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public Clear(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод очищает коллекцию
     *
     * @return
     */
    @Override
    public String execute(String login) throws InterruptedException {
        Runnable clear = () -> {
            synchronized (this) {
                try {
                    bdActivity.clearSQL(login);
                    if (manager.col.removeIf(col -> col.getLogin().equals(login))) {
                        answer = "Коллекция очищена. Удалены все принадлежащие вам элементы";
                    } else {
                        answer = "В коллекции нет элементов принадлежащих пользователю";
                    }
                } catch (SQLException e) {
                    answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                }
                notify();
            }
        };
        new Thread(clear).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}