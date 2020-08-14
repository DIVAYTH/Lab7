package commands;

import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class RemoveGreater extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public RemoveGreater(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод удаляет все элементы, превышающие заданный height
     *
     * @param str
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key, String str, String login) throws NumberFormatException, InterruptedException {
        Runnable remove = () -> {
            if (!(manager.col.size() == 0)) {
                int oldSize = manager.col.size();
                int height = Integer.parseInt(str);
                try {
                    bdActivity.deleteByHeight(height, login);
                } catch (SQLException e) {
                    poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
                }
                if (manager.col.removeIf(col -> col.getGroupAdmin().getHeight() != null && col.getGroupAdmin().getHeight() > height && col.getLogin().equals(login))) {
                    int newSize = oldSize - manager.col.size();
                    if (newSize == 1) {
                        poolSend.submit(new ServerSender(key, "Был удален " + newSize + " элемент коллекции"));
                    } else {
                        poolSend.submit(new ServerSender(key, "Было удалено " + newSize + " элемента коллекции"));
                    }
                } else {
                    poolSend.submit(new ServerSender(key, "Коллекция не изменина, так как height всех элементов меньше указанного или они принадлежат другому пользователю"));
                }
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(remove).start();
    }
}