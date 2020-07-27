package commands;

import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

public class Clear extends AbstractCommand {

    /**
     * Метод очищает коллекцию
     *
     * @return
     */
    @Override
    public String execute(String login) throws InterruptedException {
        try {
            final String[] answer = {""};
            PreparedStatement ps = CollectionManager.getManager().connect.prepareStatement("DELETE FROM studygroup WHERE login = ?;");
            ps.setString(1, login);
            ps.execute();
            Runnable clearElements = () -> {
                if (CollectionManager.getManager().col.removeIf(col -> col.getLogin().equals(login))) {
                    answer[0] = "Коллекция очищена. Удалены все принадлежащие вам элементы";
                } else {
                    answer[0] = "В коллекции нет элементов принадлежащих пользователю";
                }
            };
            Thread changeCol = new Thread(clearElements);
            changeCol.start();
            changeCol.join();
            return String.valueOf(answer[0]);
        } catch (SQLException e) {
            return "Ошибка при работе с БД (вероятно что-то с БД)";
        }
    }
}