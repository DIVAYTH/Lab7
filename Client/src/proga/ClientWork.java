package proga;

import collectionClasses.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientWork {
    private Scanner scanner = new Scanner(System.in);
    public boolean access;
    private ArrayList<File> scriptRepeat = new ArrayList<>();
    private BufferedReader commandReader;
    public boolean scriptOn;

    /**
     * Основной метод клиента (отправляет необходимый объект на сервер)
     *
     * @param socket
     * @param command
     * @param login
     * @param password
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void work(Socket socket, String command, String login, String password) throws IOException, ClassNotFoundException {
        if (command.equals("reg")) {
            Command request = new Command("reg", login, password);
            sendCommand(socket, request);
            getAnswer(socket);
        } else if (command.equals("sign")) {
            Command request = new Command("sign", login, password);
            sendCommand(socket, request);
            getAnswer(socket);
        }
        if (access) {
            while (true) {
                command = scanner.nextLine();
                choose(socket, command, login, password);
            }
        }
    }

    /**
     * Метод выбирает и отправляет команду на сервер
     *
     * @param socket
     * @param command
     * @param login
     * @param password
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void choose(Socket socket, String command, String login, String password) throws IOException, ClassNotFoundException {
        String[] finalUserCommand = command.trim().split(" ");
        if (finalUserCommand.length == 1) {
            switch (finalUserCommand[0]) {
                case "":
                    break;
                case "clear":
                case "help":
                case "info":
                case "show":
                case "print_field_ascending_students_count":
                case "print_field_descending_form_of_education": {
                    Command request = new Command(finalUserCommand[0], login, password);
                    sendCommand(socket, request);
                    getAnswer(socket);
                }
                break;
                case "add_if_max":
                case "add_if_min":
                case "add": {
                    Command request = new Command(finalUserCommand[0], add(), login, password);
                    sendCommand(socket, request);
                    getAnswer(socket);
                }
                break;
                case "exit":
                    System.exit(0);
                default:
                    System.out.println("Неизвестная команда или команда введена без аргументов. Введите снова");
            }
        } else if (finalUserCommand.length == 2) {
            switch (finalUserCommand[0]) {
                case "remove_greater":
                case "remove_by_id":
                case "remove_any_by_students_count":
                    try {
                        Integer.parseInt(finalUserCommand[1]);
                        Command request = new Command(finalUserCommand[0], finalUserCommand[1], login, password);
                        sendCommand(socket, request);
                        getAnswer(socket);
                    } catch (NumberFormatException e) {
                        System.out.println("Вы ввели строку или число выходит за пределы int. Введите снова");
                    }
                    break;
                case "update":
                    try {
                        Integer.parseInt(finalUserCommand[1]);
                        Command request = new Command(finalUserCommand[0], finalUserCommand[1], add(), login, password);
                        sendCommand(socket, request);
                        getAnswer(socket);
                    } catch (NumberFormatException e) {
                        System.out.println("Вы ввели строку или число выходит за пределы int. Введите снова");
                    }
                    break;
                case "execute_script":
                    scriptOn = true;
                    File file = new File(finalUserCommand[1]);
                    if (!file.exists())
                        System.out.println("Файла с таким именем не существует.");
                    else if (!file.canRead())
                        System.out.println("Файл защищён от чтения. Невозможно выполнить скрипт.");
                    else if (scriptRepeat.contains(file)) {
                        System.out.println("Могло произойти зацикливание при исполнении скрипта: "
                                + finalUserCommand[1] + "\nКоманда не будет выполнена. Переход к следующей команде");
                    } else {
                        scriptRepeat.add(file);
                        try {
                            commandReader = new BufferedReader(new FileReader(file));
                            String line = commandReader.readLine();
                            while (line != null) {
                                choose(socket, line, login, password);
                                System.out.println();
                                line = commandReader.readLine();
                            }
                            System.out.println("Скрипт исполнен");
                        } catch (IOException ex) {
                            System.out.println("Невозможно считать скрипт");
                        }
                        scriptRepeat.remove(scriptRepeat.size() - 1);
                    }
                    scriptOn = false;
                    break;
                default:
                    System.out.println("Неизвестная команда или команда введена без аргументов. Введите снова");
            }
        }
    }

    /**
     * Метод отправляет команду на сервер
     *
     * @param socket
     * @param answer
     * @throws IOException
     */
    public void sendCommand(Socket socket, Command answer) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream toServer = new ObjectOutputStream(baos);
        toServer.writeObject(answer);
        byte[] out = baos.toByteArray();
        socket.getOutputStream().write(out);
    }

    /**
     * Метод получает результат от сервера
     *
     * @param socket
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void getAnswer(Socket socket) throws IOException, ClassNotFoundException {
        String answer;
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());
        answer = (String) fromServer.readObject();
        switch (answer) {
            case "exit":
                System.exit(0);
            case "Авторизация прошла успешно":
                access = true;
                System.out.println("Вы успешно авторизованы. Введите help чтобы узнать список доступных команд.");
                break;
            default:
                System.out.println(answer);
                break;
        }
    }

    /**
     * Метод проверяет занчение int для add
     *
     * @param arr
     * @return
     */
    int checkInt(String arr) {
        Integer values = null;
        String str;
        Scanner scanner = new Scanner(System.in);
        while (values == null) {
            System.out.println("Введите значение " + arr);
            str = scanner.nextLine().trim();
            if (str.equals("")) {
                System.out.println(arr + " не может быть null. Введите снова");
            } else {
                try {
                    values = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    System.out.println("Вы ввели строку или число выходит за пределы. Введите снова");
                }
            }
        }
        return values;
    }

    /**
     * Метод проверяет занчение double для add
     *
     * @param arr
     * @return
     */
    Double checkDouble(String arr) {
        Double values = null;
        String str;
        Scanner scanner = new Scanner(System.in);
        while (values == null) {
            System.out.println("Введите значение " + arr);
            str = scanner.nextLine().trim();
            if (str.equals("")) {
                System.out.println(arr + " не может быть null. Введите снова");
            } else {
                try {
                    values = Double.parseDouble(str);
                } catch (NumberFormatException e) {
                    System.out.println("Вы ввели строку или число выходит за пределы. Введите снова");
                }
            }
        }
        return values;
    }

    /**
     * Метод проверяет занчение String для add
     *
     * @param arr
     * @return
     */
    String checkName(String arr) {
        Scanner scanner = new Scanner(System.in);
        String str;
        do {
            System.out.println("Введите " + arr);
            str = scanner.nextLine().trim();
            if (str.equals("")) {
                System.out.println(arr + " не может быть null. Введите снова");
            }
        } while (str.equals(""));
        return str;
    }

    /**
     * Метод проверяет занчение int для add с возможным null
     *
     * @param arr
     * @return
     */
    Integer checkIntWithNull(String arr) {
        int values = -1;
        String str;
        Scanner scanner = new Scanner(System.in);
        while (values == -1) {
            System.out.println("Введите значение " + arr);
            str = scanner.nextLine().trim();
            if (str.equals("")) {
                return null;
            } else {
                try {
                    values = Integer.parseInt(str);
                } catch (NumberFormatException e) {
                    System.out.println("Вы ввели строку или число выходит за пределы. Введите снова");
                }
            }
        }
        return values;
    }

    /**
     * Метод создает элемент для колекции
     *
     * @return
     */
    public StudyGroup add() {
        StudyGroup studyGroup;
        FormOfEducation formOfEducation = null;
        Semester semesterEnum = null;
        Color hairColor = null;
        Country nationality = null;
        Scanner scanner = new Scanner(System.in);
        String[] arr = {"Имя группы", "x", "y", "students count", "Имя главы группы", "height", "x", "y", "z"};
        if (scriptOn) {
            try {
                String[] arrScript = new String[13];
                for (int i = 0; i < arrScript.length; i++) {
                    arrScript[i] = commandReader.readLine();
                }
                String name = arrScript[0];
                Integer x = Integer.valueOf(arrScript[1]);
                Double y = Double.valueOf(arrScript[2]);
                Integer studentsCount = Integer.valueOf(arrScript[3]);
                formOfEducation = FormOfEducation.valueOf(arrScript[4]);
                semesterEnum = Semester.valueOf(arrScript[5]);
                String per_name = arrScript[6];
                Integer height = Integer.valueOf(arrScript[7]);
                hairColor = Color.valueOf(arrScript[8]);
                nationality = Country.valueOf(arrScript[9]);
                Double loc_x = Double.valueOf(arrScript[10]);
                int loc_y = Integer.parseInt(arrScript[11]);
                Integer loc_z = Integer.valueOf(arrScript[12]);
                long id = 0;
                studyGroup = new StudyGroup(id, name, new Coordinates(x, y), studentsCount, formOfEducation, semesterEnum,
                        new Person(per_name, height, hairColor, nationality, new Location(loc_x, loc_y, loc_z)), "");
            } catch (Exception e) {
                return null;
            }
        } else {
            String name = checkName(arr[0]);
            Integer x = checkInt(arr[1]);
            Double y = checkDouble(arr[2]);
            Integer studentsCount = checkIntWithNull(arr[3]);

            String s_formOfEducation = "";
            do {
                try {
                    System.out.println("Выберите форму обученя из: DISTANCE_EDUCATION, FULL_TIME_EDUCATION, EVENING_CLASSES;");
                    s_formOfEducation = scanner.nextLine().trim().toUpperCase();
                    if (s_formOfEducation.equals("")) {
                        formOfEducation = null;
                    } else {
                        formOfEducation = FormOfEducation.valueOf(s_formOfEducation);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Такой формы обучения нет");
                }
            } while (!s_formOfEducation.equals("DISTANCE_EDUCATION") && !s_formOfEducation.equals("FULL_TIME_EDUCATION") &&
                    !s_formOfEducation.equals("EVENING_CLASSES") && !s_formOfEducation.equals(""));

            String s_semesterEnum = "";
            do {
                try {
                    System.out.println("Выберите семестр из: FIRST, THIRD, FIFTH, EIGHTH;");
                    s_semesterEnum = scanner.nextLine().trim().toUpperCase();
                    if (s_semesterEnum.equals("")) {
                        semesterEnum = null;
                    } else {
                        semesterEnum = Semester.valueOf(s_semesterEnum);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Такого семестра нет");
                }
            } while (!s_semesterEnum.equals("FIRST") && !s_semesterEnum.equals("THIRD") && !s_semesterEnum.equals("FIFTH")
                    && !s_semesterEnum.equals("EIGHTH") && !s_semesterEnum.equals(""));

            String per_name = checkName(arr[4]);
            Integer height = checkIntWithNull(arr[5]);

            String s_hairColor = "";
            do {
                try {
                    System.out.println("Выберите цвет волос: RED, BLUE, YELLOW, ORANGE, WHITE");
                    s_hairColor = scanner.nextLine().trim().toUpperCase();
                    if (s_hairColor.equals("")) {
                        hairColor = null;
                    } else {
                        hairColor = Color.valueOf(s_hairColor);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Такого цвета нет. Введите снова");
                }
            } while (!s_hairColor.equals("RED") && !s_hairColor.equals("BLUE") && !s_hairColor.equals("YELLOW") && !s_hairColor.equals("ORANGE")
                    && !s_hairColor.equals("WHITE") && !s_hairColor.equals(""));

            String s_nationality = "";
            do {
                try {
                    System.out.println("Выберите откуда она:  USA, CHINA, INDIA, VATICAN");
                    s_nationality = scanner.nextLine().trim().toUpperCase();
                    if (s_nationality.equals("")) {
                        System.out.println("nationality не может быть null. Введите снова");
                    } else {
                        nationality = Country.valueOf(s_nationality);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Такой страны нет. Введите снова");
                }
            } while (!s_nationality.equals("USA") && !s_nationality.equals("CHINA") &&
                    !s_nationality.equals("INDIA") && !s_nationality.equals("VATICAN"));

            Double loc_x = checkDouble(arr[6]);
            int loc_y = checkInt(arr[7]);
            Integer loc_z = checkInt(arr[8]);
            long id = 0;
            studyGroup = new StudyGroup(id, name, new Coordinates(x, y), studentsCount, formOfEducation, semesterEnum,
                    new Person(per_name, height, hairColor, nationality, new Location(loc_x, loc_y, loc_z)), "");
        }
        return studyGroup;
    }
}