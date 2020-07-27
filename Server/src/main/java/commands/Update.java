package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Update extends AbstractCommand {

    /**
     * Метод обновляет элемент по его id
     *
     * @param str
     * @param studyGroup
     * @return
     */
    @Override
    public String execute(String str, StudyGroup studyGroup, String login) throws NumberFormatException, InterruptedException {
        final String[] answer = {""};
        if (!(studyGroup == null)) {
            if (!(CollectionManager.getManager().col.size() == 0)) {
                int id = Integer.parseInt(str);
                try {
                    PreparedStatement ps = CollectionManager.getManager().connect.prepareStatement("UPDATE studygroup SET name = ? , x = ? , y = ?" +
                            ", creationDate = ?, studentsCount = ? , formOfEducation = ?, semesterEnum = ?, pername = ?, height = ?, hairColor = ? " +
                            ", nationality = ?, locX = ?, locY = ?, locZ = ? WHERE id = ? AND login = ?;");
                    ps.setString(1, studyGroup.getName());
                    ps.setInt(2, studyGroup.getCoordinates().getX());
                    ps.setDouble(3, studyGroup.getCoordinates().getY());
                    ps.setObject(4, studyGroup.getCreationDate());
                    ps.setInt(5, studyGroup.getStudentsCount());
                    ps.setString(6, String.valueOf(studyGroup.getFormOfEducation()));
                    ps.setObject(7, String.valueOf(studyGroup.getSemesterEnum()));
                    ps.setString(8, studyGroup.getGroupAdmin().getName());
                    ps.setInt(9, studyGroup.getGroupAdmin().getHeight());
                    ps.setObject(10, String.valueOf(studyGroup.getGroupAdmin().getHairColor()));
                    ps.setObject(11, String.valueOf(studyGroup.getGroupAdmin().getNationality()));
                    ps.setDouble(12, studyGroup.getGroupAdmin().getLocation().getX());
                    ps.setInt(13, studyGroup.getGroupAdmin().getLocation().getY());
                    ps.setInt(14, studyGroup.getGroupAdmin().getLocation().getZ());
                    ps.setInt(15, id);
                    ps.setString(16, login);
                    ps.execute();
                } catch (SQLException e) {
                    return "Ошибка при работе с БД (вероятно что-то с БД)";
                }
                Runnable updateElement = () -> {
                    if (CollectionManager.getManager().col.removeIf(col -> col.getId() == id && col.getLogin().equals(login))) {
                        studyGroup.setId(id);
                        studyGroup.setLogin(login);
                        CollectionManager.getManager().col.add(studyGroup);
                        answer[0] = "Элемент обновлен";
                    } else {
                        answer[0] = "Элемента с таким id нет или пользователь не имеет доступа к этому элементу";
                    }
                };
                Thread changeCol = new Thread(updateElement);
                changeCol.start();
                changeCol.join();
                return String.valueOf(answer[0]);
            }
            return "Коллекция пуста";
        } else {
            return "Ошибка при добавлении элемента. Поля указаны не верно";
        }
    }
}