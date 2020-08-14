package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.security.NoSuchAlgorithmException;;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class ServerHandler {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    /**
     * Метод регестрирует, авторизует пользователя или выполняет команду
     */
    public void handler(Command command, CollectionManager manager, BDActivity bdActivity, ExecutorService poolSend, SelectionKey key) {
        try {
            if (command.getName().equals("reg")) {
                poolSend.submit(new ServerSender(key, bdActivity.registration(command)));
            } else if (command.getName().equals("sign")) {
                if (bdActivity.authorization(command)) {
                    logger.debug("Пользователь с логином " + command.getLogin() + " успешно авторизован.");
                    poolSend.submit(new ServerSender(key, "Авторизация прошла успешно"));
                } else {
                    logger.debug("Пользователь ввел не верный пароль");
                    poolSend.submit(new ServerSender(key, "Логин или пароль введены неверно"));
                }
            } else {
                if (bdActivity.authorization(command)) {
                    switch (command.getName()) {
                        case "clear": {
                            manager.commandMap.get(command.getName()).executeCommand(poolSend, key, command.getLogin());
                        }
                        break;
                        case "show":
                        case "info":
                        case "help":
                        case "print_field_ascending_students_count":
                        case "print_field_descending_form_of_education": {
                            manager.commandMap.get(command.getName()).executeCommand(poolSend, key);
                        }
                        break;
                        case "remove_greater":
                        case "remove_by_id":
                        case "remove_any_by_students_count":
                        case "execute_script": {
                            manager.commandMap.get(command.getName()).executeCommand(poolSend, key, command.getArgs(), command.getLogin());
                        }
                        break;
                        case "add_if_max":
                        case "add_if_min":
                        case "add": {
                            manager.commandMap.get(command.getName()).executeCommand(poolSend, key, command.getStudyGroup(), command.getLogin());
                        }
                        break;
                        case "update": {
                            manager.commandMap.get(command.getName()).executeCommand(poolSend, key, command.getArgs(), command.getStudyGroup(), command.getLogin());
                        }
                    }
                    logger.debug("Обработана команда " + command.getName());
                } else {
                    logger.debug("Кто-то обошел защиту сервера");
                    poolSend.submit(new ServerSender(key, "О вы юный хакер"));
                }
            }
        } catch (NoSuchAlgorithmException | ExecutionException | InterruptedException | UnsupportedEncodingException e) {
            // Все под контролем
        }
    }
}