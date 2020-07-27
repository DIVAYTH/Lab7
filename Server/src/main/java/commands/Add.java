package commands;

import collectionClasses.StudyGroup;
import proga.CollectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Add extends AbstractCommand {

    /**
     * Метод добавляет элемент в БД
     *
     * @param studyGroup
     * @return
     */
    @Override
    public String execute(StudyGroup studyGroup, String login) throws InterruptedException {
        try {
            PreparedStatement ps = CollectionManager.getManager().connect.prepareStatement("INSERT INTO studygroup (id, name, x, y, " +
                    "creationDate, studentsCount, formOfEducation, semesterEnum, pername, height, hairColor, nationality, locX, locY, locZ, login) " +
                    "VALUES (nextval('idSGsequence'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
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
            ps.setString(15, login);
            ps.execute();
        } catch (SQLException e) {
            return "Ошибка при работе с БД (вероятно что-то с БД)";
        }
        studyGroup.setId(++CollectionManager.getManager().id);
        studyGroup.setLogin(login);
        Runnable addElement = () -> {
            CollectionManager.getManager().col.add(studyGroup);
        };
        Thread changeCol = new Thread(addElement);
        changeCol.start();
        changeCol.join();
        return "Элемент коллекции добавлен";
    }
}
