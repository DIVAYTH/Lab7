package commands;

import collectionClasses.StudyGroup;
import proga.BDActivity;
import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update extends AbstractCommand {
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String answer;

    public Update(CollectionManager manager, BDActivity bdActivity) {
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод обновляет элемент по его id
     *
     * @param str
     * @param studyGroup
     * @return
     */
    @Override
    public String execute(String str, StudyGroup studyGroup, String login) throws NumberFormatException, InterruptedException {
        Runnable update = () -> {
            synchronized (this) {
                try {
                    if (!(studyGroup == null)) {
                        if (!(manager.col.size() == 0)) {
                            int id = Integer.parseInt(str);
                            bdActivity.update(id, login);
                            if (manager.col.removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                                studyGroup.setId(id);
                                studyGroup.setLogin(login);
                                manager.col.add(studyGroup);
                                answer = "Элемент обновлен";
                            } else {
                                answer = "Элемента с таким id нет или пользователь не имеет доступа к этому элементу";
                            }
                        } else {
                            answer = "Коллекция пуста";
                        }
                    } else {
                        answer = "Ошибка при добавлении элемента. Поля указаны не верно";
                    }
                } catch (SQLException e) {
                    answer = "Ошибка при работе с БД (вероятно что-то с БД)";
                } catch (NullPointerException e) {
                    answer = "Ошибка при добавлении элемента. Поля в файле указаны не верно";
                }
                notify();
            }
        };
        new Thread(update).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}