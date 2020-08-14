package commands;

import proga.BDActivity;
import proga.CollectionManager;
import proga.ServerSender;

import java.nio.channels.SelectionKey;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;

public class RemoveStudentsCount extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public RemoveStudentsCount(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод удаляет эемент по его student count
     *
     * @param str
     * @return
     */
    @Override
    public void executeCommand(ExecutorService poolSend, SelectionKey key, String str, String login) throws NumberFormatException, InterruptedException {
        Runnable delete = () -> {
            if (!(manager.col.size() == 0)) {
                int studentsCount = Integer.parseInt(str);
                try {
                    bdActivity.deleteByStudentsCount(studentsCount, login);
                } catch (SQLException e) {
                    poolSend.submit(new ServerSender(key, "Ошибка при работе с БД (вероятно что-то с БД)"));
                }
                if (manager.col.removeIf(col -> col.getStudentsCount() != null && col.getStudentsCount() == studentsCount && col.getLogin().equals(login))) {
                    poolSend.submit(new ServerSender(key, "Элемент удален"));
                } else
                    poolSend.submit(new ServerSender(key, "Нет элемента с таким student_count или пользователь не имеет доступа к этому элементу"));
            } else {
                poolSend.submit(new ServerSender(key, "Коллекция пуста"));
            }
        };
        new Thread(delete).start();
    }
}