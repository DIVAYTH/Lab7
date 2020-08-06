package commands;

import proga.BDActivity;
import proga.CollectionManager;

import java.sql.SQLException;

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
    public String execute(String str, String login) throws NumberFormatException, InterruptedException {
        Runnable delete = () -> {
            synchronized (this) {
                if (!(manager.col.size() == 0)) {
                    int studentsCount = Integer.parseInt(str);
                    try {
                        bdActivity.deleteByStudentsCount(studentsCount, login);
                    } catch (SQLException e) {
                        answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                    }
                    if (manager.col.removeIf(col -> col.getStudentsCount() != null && col.getStudentsCount() == studentsCount && col.getLogin().equals(login))) {
                        answer = "Элемент удален";
                    } else
                        answer = "Нет элемента с таким student_count или пользователь не имеет доступа к этому элементу";

                } else {
                    answer = "Коллекция пуста";
                }
                notify();
            }
        };
        new Thread(delete).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}