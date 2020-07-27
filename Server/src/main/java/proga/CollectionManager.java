package proga;

import collectionClasses.*;
import commands.*;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionManager {
    public Map<String, AbstractCommand> commandMap;
    private static CollectionManager manager = new CollectionManager();
    public Collection<StudyGroup> col = Collections.synchronizedCollection(new PriorityQueue<>());
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);
    public Statement statement;
    public Connection connect;
    public long id;

    /**
     * Мето предоставляет общий доступ к классу коллекций
     *
     * @return
     */
    public static CollectionManager getManager() {
        return manager;
    }

    {
        commandMap = new HashMap<>();
        commandMap.put("clear", new Clear());
        commandMap.put("show", new Show());
        commandMap.put("info", new Info());
        commandMap.put("help", new Help());
        commandMap.put("remove_any_by_students_count", new RemoveStudentsCount());
        commandMap.put("print_field_ascending_students_count", new PrintStudentsCount());
        commandMap.put("print_field_descending_form_of_education", new PrintFormOfEducation());
        commandMap.put("remove_by_id", new RemoveId());
        commandMap.put("add", new Add());
        commandMap.put("remove_greater", new RemoveGreater());
        commandMap.put("add_if_min", new AddIfMin());
        commandMap.put("add_if_max", new AddIfMax());
        commandMap.put("update", new Update());
        commandMap.put("execute_script", new ExecuteScript());
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        logger.debug("Запуск сервера");
        ServerConnection serverConnection = new ServerConnection();
        try {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    logger.debug("Отключение сервера");
                }
            });
            serverConnection.connection(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error("Вы не ввели имя файла");
        } catch (NoSuchElementException | InterruptedException e) {
            //Для ctrl+D
        }
    }

    /**
     * Метод подключает сервер к SQl и загружает данные из SQL таблицы и помещает из в коллекцию
     */
    public void load(String file) throws ClassNotFoundException {
        try {
            FileInputStream bd = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(bd);
            String url = properties.getProperty("BD.location");
            String login = properties.getProperty("BD.login");
            String password = properties.getProperty("BD.password");
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection(url, login, password);
            statement = connect.createStatement();
            ResultSet res = statement.executeQuery("SELECT * FROM studygroup;");
            FormOfEducation formOfEducation;
            Semester semesterEnum;
            Color hairColor;
            while (res.next()) {
                id = res.getLong("id");
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
                StudyGroup studyGroup = new StudyGroup(id, name, new Coordinates(x, y), studentsCount, formOfEducation, semesterEnum,
                        new Person(perName, height, hairColor, nationality, new Location(locX, locY, locZ)), loginSG);
                col.add(studyGroup);
            }
            logger.debug("Сервер подключился к БД");
        } catch (SQLException e) {
            logger.debug("Ошибка при добавлении элемента в БД (вероятно что-то с БД)");
        } catch (IOException e) {
            logger.debug("Файл не найден");
        }
    }
}