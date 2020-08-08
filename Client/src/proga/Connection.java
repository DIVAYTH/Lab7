package proga;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Connection {
    private ClientWork client = new ClientWork();
    private Scanner scanner = new Scanner(System.in);

    /**
     * Метод реализует соединение между клиентом и сервером
     */
    public void connection() throws ClassNotFoundException {
        while (true) {
            try {
                System.out.println("Введите порт");
                int port = Integer.parseInt(scanner.nextLine());
                System.out.println("Введите хост");
                String host = scanner.nextLine();
                System.out.println("Соединение... Ожидайте");
                SocketAddress socketAddress = new InetSocketAddress(host, port);
                try (Socket socket = new Socket()) {
                    socket.connect(socketAddress, 5000);
                    System.out.println("Соединение установленно");
                    client.getAnswer(socket);
                    while (true) {
                        client.work(socket);
                    }
                }
            } catch (SocketTimeoutException e) {
                System.out.println("Время подключения вышло. Хост указан неверно или сервер недоступен");
            } catch (NumberFormatException e) {
                System.out.println("Порт не число или выходит за пределы");
            } catch (ConnectException e) {
                System.out.println("Порт не найден или недоступен");
            } catch (UnknownHostException e) {
                System.out.println("Хост введен неверно");
            } catch (IllegalArgumentException e) {
                System.out.println("Порт должен принимать значения от 1 до 65535");
            } catch (IOException e) {
                System.out.println("Нет связи с сервером. Подключиться ещё раз введите (да) или (нет)?");
                String answer;
                while (!(answer = scanner.nextLine()).equals("да")) {
                    switch (answer) {
                        case "":
                            break;
                        case "нет":
                            System.exit(0);
                            break;
                        default:
                            System.out.println("Введите корректное значение.");
                    }
                }
            }
        }
    }
}