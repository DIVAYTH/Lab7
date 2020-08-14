package commands;

import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

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
    public void executeCommand(ExecutorService poolSend, SelectionKey key, String login) throws InterruptedException {
        Runnable clear = () -> {
            try {
                bdActivity.clearSQL(login);
                if (manager.col.removeIf(col -> col.getLogin().equals(login))) {
                    poolSend.submit(new ServerSender(key, "Коллекция очищена. Удалены все принадлежащие вам элементы"));
                } else {
                    poolSend.submit(new ServerSender(key, "В коллекции нет элементов принадлежащих пользователю"));
                }
            } catch (SQLException e) {
                poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
            }
        };
        new Thread(clear).start();
    }
}