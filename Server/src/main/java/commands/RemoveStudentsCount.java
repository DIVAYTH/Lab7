package commands;

import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveStudentsCount extends AbstractCommand {

    /**
     * Метод удаляет эемент по его student count
     *
     * @param str
     * @return
     */
    @Override
    public String execute(String str, String login) throws NumberFormatException, InterruptedException {
        final String[] answer = {""};
        if (!(CollectionManager.getManager().col.size() == 0)) {
            int students_count = Integer.parseInt(str);
            try {
                PreparedStatement ps = CollectionManager.getManager().connect.prepareStatement("DELETE FROM studygroup WHERE(studentsCount = ?) AND (login = ?)");
                ps.setInt(1, students_count);
                ps.setString(2, login);
                ps.execute();
            } catch (SQLException e) {
                return "Ошибка при работе с БД (вероятно что-то с БД)";
            }
            Runnable removeElement = () -> {
                if (CollectionManager.getManager().col.removeIf(col -> col.getStudentsCount() != null && col.getStudentsCount() == students_count && col.getLogin().equals(login))) {
                    answer[0] = "Элемент удален";
                } else
                    answer[0] = "Нет элемента с таким student_count или пользователь не имеет доступа к этому элементу";
            };
            Thread changeCol = new Thread(removeElement);
            changeCol.start();
            changeCol.join();
            return String.valueOf(answer[0]);
        }
        return "Коллекция пуста";
    }
}