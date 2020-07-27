package commands;

import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveGreater extends AbstractCommand {

    /**
     * Метод удаляет все элементы, превышающие заданный height
     *
     * @param str
     * @return
     */
    @Override
    public String execute(String str, String login) throws NumberFormatException, InterruptedException {
        final String[] answer = {""};
        if (!(CollectionManager.getManager().col.size() == 0)) {
            int oldSize = CollectionManager.getManager().col.size();
            int height = Integer.parseInt(str);
            try {
                PreparedStatement ps = CollectionManager.getManager().connect.prepareStatement("DELETE FROM studygroup WHERE(height < ?) AND (login = ?)");
                ps.setInt(1, height);
                ps.setString(2, login);
                ps.execute();
            } catch (SQLException e) {
                return "Ошибка при работе с БД (вероятно что-то с БД)";
            }
            Runnable removeElement = () -> {
                if (CollectionManager.getManager().col.removeIf(col -> col.getGroupAdmin().getHeight() != null && col.getGroupAdmin().getHeight() < height && col.getLogin().equals(login))) {
                    int newSize = oldSize - CollectionManager.getManager().col.size();
                    if (newSize == 1) {
                        answer[0] = "Был удален " + newSize + " элемент коллекции";
                    } else {
                        answer[0] = "Было удалено " + newSize + " элемента коллекции";
                    }
                } else {
                    answer[0] = "Коллекция не изменина, так как height всех элементов меньше указанного или они принадлежат другому пользователю";
                }
            };
            Thread changeCol = new Thread(removeElement);
            changeCol.start();
            changeCol.join();
            return String.valueOf(answer[0]);
        } else {
            return "Коллекция пуста";
        }
    }
}