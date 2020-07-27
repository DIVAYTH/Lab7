package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.channels.SelectionKey;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ServerHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private Command command;
    private ObjectOutputStream toClient = new ObjectOutputStream(baos);

    public ServerHandler(Command command) throws IOException {
        this.command = command;
    }

    public ByteArrayOutputStream getBaos() {
        return baos;
    }

    /**
     * Метод регестрирует или авторизует пользователя или отправляет команду на исполнение
     */
    public void run() {
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA-224");
            int size = baos.size();
            if (command.getName().equals("reg")) {
                PreparedStatement ps = CollectionManager.getManager().connect.
                        prepareStatement("INSERT INTO studygroup_login_password (login, password) VALUES (?, ?);");
                ps.setString(1, command.getLogin());
                ps.setString(2, Base64.getEncoder().encodeToString(hash.digest(command.getPassword().getBytes("UTF-8"))));
                ps.execute();
                toClient.writeObject("Регистрация прошла успешно");
                logger.debug("Пользователь с логином " + command.getLogin() + " успешно зарегестрирован");
            } else if (command.getName().equals("sign")) {
                ResultSet res = CollectionManager.getManager().statement.executeQuery("SELECT * FROM studygroup_login_password;");
                while (res.next()) {
                    if (command.getLogin().equals(res.getString("login")) && Base64.getEncoder()
                            .encodeToString(hash.digest(command.getPassword().getBytes("UTF-8")))
                            .equals(res.getString("password"))) {
                        toClient.writeObject("Авторизация прошла успешно");
                        logger.debug("Пользователь с логином " + command.getLogin() + " успешно авторизован.");
                    }
                }
                if (size == baos.size()) {
                    toClient.writeObject("Логин или пароль введены неверно");
                    logger.debug("Пользователь ввел не верный пароль");
                }
            } else {
                chooseCommand();
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            try {
                toClient.writeObject("Ошибка с БД " + " 1.слишком большой пароль или логин\n" + "2. такие данны уже есть в БД");
            } catch (IOException ignored) {
            }
        } catch (IOException | InterruptedException ignored) {
        }
    }

    /**
     * Метод обрабатывает полученную команду
     */
    public void chooseCommand() throws InterruptedException, IOException {
        switch (command.getName()) {
            case "clear": {
                toClient.writeObject(CollectionManager.getManager().commandMap.get(command.getName()).execute(command.getLogin()));
            }
            break;
            case "show":
            case "info":
            case "help":
            case "print_field_ascending_students_count":
            case "print_field_descending_form_of_education": {
                toClient.writeObject(CollectionManager.getManager().commandMap.get(command.getName()).execute());
            }
            break;
            case "remove_greater":
            case "remove_by_id":
            case "remove_any_by_students_count":
            case "execute_script": {
                toClient.writeObject(CollectionManager.getManager().commandMap.get(command.getName()).execute(command.getArgs(), command.getLogin()));
            }
            break;
            case "add_if_max":
            case "add_if_min":
            case "add": {
                toClient.writeObject(CollectionManager.getManager().commandMap.get(command.getName()).execute(command.getStudyGroup(), command.getLogin()));
            }
            break;
            case "update": {
                toClient.writeObject(CollectionManager.getManager().commandMap.get(command.getName()).execute(command.getArgs(), command.getStudyGroup(), command.getLogin()));
            }
            break;
        }
        baos.close();
        toClient.close();
        logger.debug("Обработана команда " + command.getName());
    }
}