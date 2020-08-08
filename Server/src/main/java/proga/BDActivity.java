package proga;

import collectionClasses.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Base64;
import java.util.Properties;

public class BDActivity {
    private ResultSet res;
    private Connection connect;
    private PreparedStatement ps;
    private Statement statement;
    private StudyGroup studyGroup;
    private MessageDigest hash;
    private static final Logger logger = LoggerFactory.getLogger(BDActivity.class);

    /**
     * Метод подключает сервер к БД и загружает данные из таблицы
     *
     * @param file
     * @return
     * @throws ClassNotFoundException
     */
    public StudyGroup loadFromSQL(String file) throws ClassNotFoundException, IOException, SQLException, NullPointerException {
        FileInputStream bd = new FileInputStream(file);
        Properties properties = new Properties();
        properties.load(bd);
        String url = properties.getProperty("BD.location");
        String login = properties.getProperty("BD.login");
        String password = properties.getProperty("BD.password");
        Class.forName("org.postgresql.Driver");
        connect = DriverManager.getConnection(url, login, password);
        statement = connect.createStatement();
        res = statement.executeQuery("SELECT * FROM studygroup;");
        FormOfEducation formOfEducation;
        Semester semesterEnum;
        Color hairColor;
        while (res.next()) {
            long id = res.getLong("id");
            String name = res.getString("name");
            Integer x = res.getInt("x");
            double y = res.getDouble("y");
            Integer studentsCount = res.getInt("studentsCount");
            try {
                formOfEducation = FormOfEducation.valueOf(res.getString("formOfEducation"));
            } catch (IllegalArgumentException e) {
                formOfEducation = null;
            }
            try {
                semesterEnum = Semester.valueOf(res.getString("semesterEnum"));
            } catch (IllegalArgumentException e) {
                semesterEnum = null;
            }
            String perName = res.getString("pername");
            Integer height = res.getInt("height");
            try {
                hairColor = Color.valueOf(res.getString("hairColor"));
            } catch (IllegalArgumentException e) {
                hairColor = null;
            }
            Country nationality = Country.valueOf(res.getString("nationality"));
            double locX = res.getDouble("locX");
            int locY = res.getInt("locY");
            Integer locZ = res.getInt("locZ");
            String loginSG = res.getString("login");
            studyGroup = new StudyGroup(id, name, new Coordinates(x, y), studentsCount, formOfEducation, semesterEnum,
                    new Person(perName, height, hairColor, nationality, new Location(locX, locY, locZ)), loginSG);
        }
        logger.debug("Сервер подключился к БД");
        return studyGroup;
    }

    /**
     * Метод регестрирует пользователя в БД
     *
     * @param command
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public String registration(Command command) throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException {
        hash = MessageDigest.getInstance("SHA-224");
        PreparedStatement ps = connect.prepareStatement("INSERT INTO studygroup_login_password (login, password) VALUES (?, ?);");
        ps.setString(1, command.getLogin());
        ps.setString(2, Base64.getEncoder().encodeToString(hash.digest(command.getPassword().getBytes("UTF-8"))));
        ps.execute();
        logger.debug("Пользователь с логином " + command.getLogin() + " успешно зарегестрирован");
        return "Регистрация прошла успешно";
    }

    /**
     * Метод авторизует пользователя
     *
     * @param command
     * @return
     * @throws UnsupportedEncodingException
     */
    public String authorization(Command command) throws UnsupportedEncodingException, SQLException, NoSuchAlgorithmException {
        hash = MessageDigest.getInstance("SHA-224");
        res = statement.executeQuery("SELECT * FROM studygroup_login_password;");
        while (res.next()) {
            if (command.getLogin().equals(res.getString("login")) && Base64.getEncoder()
                    .encodeToString(hash.digest(command.getPassword().getBytes("UTF-8")))
                    .equals(res.getString("password"))) {
                logger.debug("Пользователь с логином " + command.getLogin() + " успешно авторизован.");
                return "Авторизация прошла успешно";
            }
        }
        logger.debug("Пользователь ввел не верный пароль");
        return "Логин или пароль введены неверно";
    }

    /**
     * Метод добавляет элемент в БД
     *
     * @param studyGroup
     * @param login
     * @throws SQLException
     */
    public void addToSQL(StudyGroup studyGroup, String login) throws SQLException, NullPointerException {
        ps = connect.prepareStatement("INSERT INTO studygroup (id, name, x, y, " +
                "creationDate, studentsCount, formOfEducation, semesterEnum, pername, height, hairColor, nationality, locX, locY, locZ, login) " +
                "VALUES (nextval('idSGsequence'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        ps.setString(1, studyGroup.getName());
        ps.setInt(2, studyGroup.getCoordinates().getX());
        ps.setDouble(3, studyGroup.getCoordinates().getY());
        ps.setObject(4, studyGroup.getCreationDate());
        try {
            ps.setInt(5, studyGroup.getStudentsCount());
        } catch (NullPointerException e) {
            ps.setObject(5, null);
        }
        ps.setString(6, String.valueOf(studyGroup.getFormOfEducation()));
        ps.setObject(7, String.valueOf(studyGroup.getSemesterEnum()));
        ps.setString(8, studyGroup.getGroupAdmin().getName());
        try {
            ps.setInt(9, studyGroup.getGroupAdmin().getHeight());
        } catch (NullPointerException e) {
            ps.setObject(9, null);
        }
        ps.setObject(10, String.valueOf(studyGroup.getGroupAdmin().getHairColor()));
        ps.setObject(11, String.valueOf(studyGroup.getGroupAdmin().getNationality()));
        ps.setDouble(12, studyGroup.getGroupAdmin().getLocation().getX());
        ps.setInt(13, studyGroup.getGroupAdmin().getLocation().getY());
        ps.setInt(14, studyGroup.getGroupAdmin().getLocation().getZ());
        ps.setString(15, login);
        ps.execute();
    }

    /**
     * Метод получает максимальный id
     *
     * @return
     * @throws SQLException
     */
    public long getMAXId() throws SQLException {
        res = statement.executeQuery("SELECT MAX(id) FROM studygroup");
        res.next();
        long id = res.getLong(1);
        return id;
    }

    /**
     * Метод удаляет все элементы из SQL принадлежавшие одному пользователю
     *
     * @param login
     * @throws SQLException
     */
    public void clearSQL(String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM studygroup WHERE login = ?;");
        ps.setString(1, login);
        ps.execute();
    }

    /**
     * Удаляет все элементы из БД если они превышают указанный height
     *
     * @param height
     * @param login
     * @throws SQLException
     */
    public void deleteByHeight(int height, String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM studygroup WHERE(height > ?) AND (login = ?)");
        ps.setInt(1, height);
        ps.setString(2, login);
        ps.execute();
    }

    /**
     * Удаляет элемент из БД по его id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void deleteById(int id, String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM studygroup WHERE(id = ?) AND (login = ?)");
        ps.setInt(1, id);
        ps.setString(2, login);
        ps.execute();
    }

    /**
     * Удаляет элемент из БД по его students count
     *
     * @param studentsCount
     * @param login
     * @throws SQLException
     */
    public void deleteByStudentsCount(int studentsCount, String login) throws SQLException {
        ps = connect.prepareStatement("DELETE FROM studygroup WHERE(studentsCount = ?) AND (login = ?)");
        ps.setInt(1, studentsCount);
        ps.setString(2, login);
        ps.execute();
    }

    /**
     * Метод обновляет в БД элемент по его id
     *
     * @param id
     * @param login
     * @throws SQLException
     */
    public void update(int id, String login) throws SQLException, NullPointerException {
        ps = connect.prepareStatement("UPDATE studygroup SET name = ? , x = ? , y = ?" +
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
    }
}