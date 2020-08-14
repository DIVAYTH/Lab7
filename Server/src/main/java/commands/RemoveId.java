package commands;

import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class RemoveId extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public RemoveId(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод удаляет элемент по его id
     *
     * @param str
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key, String str, String login) throws NumberFormatException, InterruptedException {
        Runnable delete = () -> {
            if (!(manager.col.size() == 0)) {
                int id = Integer.parseInt(str);
                try {
                    bdActivity.deleteById(id, login);
                } catch (SQLException e) {
                    poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
                }
                if (manager.col.removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                    poolSend.submit(new ServerSender(key, "Элемент удален"));
                } else
                    poolSend.submit(new ServerSender(key, "Нет элемента с таким id или пользователь не имеет доступа к этому элементу"));
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(delete).start();
    }
}