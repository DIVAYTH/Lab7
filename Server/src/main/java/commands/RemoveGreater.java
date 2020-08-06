package commands;

import proga.BDActivity;
import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public String execute(String str, String login) throws NumberFormatException, InterruptedException {
        Runnable remove = () -> {
            synchronized (this) {
                if (!(manager.col.size() == 0)) {
                    int oldSize = manager.col.size();
                    int height = Integer.parseInt(str);
                    try {
                        bdActivity.deleteByHeight(height, login);
                    } catch (SQLException e) {
                        answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                    }
                    if (manager.col.removeIf(col -> col.getGroupAdmin().getHeight() != null && col.getGroupAdmin().getHeight() > height && col.getLogin().equals(login))) {
                        int newSize = oldSize - manager.col.size();
                        if (newSize == 1) {
                            answer = "Был удален " + newSize + " элемент коллекции";
                        } else {
                            answer = "Было удалено " + newSize + " элемента коллекции";
                        }
                    } else {
                        answer = "Коллекция не изменина, так как height всех элементов меньше указанного или они принадлежат другому пользователю";
                    }
                } else {
                    answer = "Коллекция пуста";
                }
                notify();
            }
        };
        new Thread(remove).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}