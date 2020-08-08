package proga;

import collectionClasses.*;
import commands.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionManager {
    public Map<String, AbstractCommand> commandMap;
    public Collection<StudyGroup> col = Collections.synchronizedCollection(new PriorityQueue<>());
    private static final Logger logger = LoggerFactory.getLogger(CollectionManager.class);

    /**
     * Основной метод сервера
     *
     * @param args
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
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
        } catch (NoSuchElementException e) {
            //Для ctrl+D
        }
    }

    /**
     * Метод инициализирует команды
     *
     * @param manager
     * @param bdActivity
     */
    public void loadCommands(CollectionManager manager, BDActivity bdActivity) {
        commandMap = new HashMap<>();
        commandMap.put("clear", new Clear(manager, bdActivity));
        commandMap.put("show", new Show(manager));
        commandMap.put("info", new Info(manager));
        commandMap.put("help", new Help());
        commandMap.put("remove_any_by_students_count", new RemoveStudentsCount(manager, bdActivity));
        commandMap.put("print_field_ascending_students_count", new PrintStudentsCount(manager));
        commandMap.put("print_field_descending_form_of_education", new PrintFormOfEducation(manager));
        commandMap.put("remove_by_id", new RemoveId(manager, bdActivity));
        commandMap.put("add", new Add(manager, bdActivity));
        commandMap.put("remove_greater", new RemoveGreater(manager, bdActivity));
        commandMap.put("add_if_min", new AddIfMin(manager, bdActivity));
        commandMap.put("add_if_max", new AddIfMax(manager, bdActivity));
        commandMap.put("update", new Update(manager, bdActivity));
        commandMap.put("execute_script", new ExecuteScript(manager));
    }

    /**
     * Метод помещает данные из SQL таблицы в коллекцию
     *
     * @return
     */
    public String loadToCol(String file, BDActivity bdActivity) throws ClassNotFoundException {
        try {
            col.add(bdActivity.loadFromSQL(file));
        } catch (SQLException e) {
            logger.error("Сервер не подключился к БД");
            return "Ошибка сервер не может подключиться к БД (вероятно что-то с БД)";
        } catch (IOException e) {
            logger.error("Сервер не подключился к БД");
            return "Файл с данными БД не найден";
        } catch (NullPointerException e) {
            return null;
        }
        return null;
    }
}