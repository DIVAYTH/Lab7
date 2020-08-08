package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.NoSuchAlgorithmException;;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class ServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private Command command;
    private CollectionManager manager;
    private BDActivity bdActivity;
    private String result;

    public ServerHandler(Command command, CollectionManager manager, BDActivity bdActivity) {
        this.command = command;
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод регестрирует или авторизует пользователя или отправляет команду на исполнение
     */
    public String handler() {
        try {
            if (command.getName().equals("reg")) {
                return bdActivity.registration(command);
            } else if (command.getName().equals("sign")) {
                return bdActivity.authorization(command);
            } else {
                switch (command.getName()) {
                    case "clear": {
                        result = manager.commandMap.get(command.getName()).execute(command.getLogin());
                    }
                    break;
                    case "show":
                    case "info":
                    case "help":
                    case "print_field_ascending_students_count":
                    case "print_field_descending_form_of_education": {
                        result = manager.commandMap.get(command.getName()).execute();
                    }
                    break;
                    case "remove_greater":
                    case "remove_by_id":
                    case "remove_any_by_students_count":
                    case "execute_script": {
                        result = manager.commandMap.get(command.getName()).execute(command.getArgs(), command.getLogin());
                    }
                    break;
                    case "add_if_max":
                    case "add_if_min":
                    case "add": {
                        result = manager.commandMap.get(command.getName()).execute(command.getStudyGroup(), command.getLogin());
                    }
                    break;
                    case "update": {
                        result = manager.commandMap.get(command.getName()).execute(command.getArgs(), command.getStudyGroup(), command.getLogin());
                    }
                }
                logger.debug("Обработана команда " + command.getName());
            }
        } catch (NoSuchAlgorithmException | ExecutionException | InterruptedException | UnsupportedEncodingException e) {
            // Все под контролем
        } catch (SQLException e) {
            return "Ошибка при работе с БД (вероятно что-то с БД)";
        }
        return result;
    }
}