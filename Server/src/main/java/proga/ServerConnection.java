package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.*;

public class ServerConnection {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);
    private Scanner scanner = new Scanner(System.in);
    private Command command;
    private ServerHandler answer;

    /**
     * Метод реализует соединение и работу с клиентом
     *
     * @throws IOException
     */
    public void connection(String file) throws IOException, ClassNotFoundException, InterruptedException {
        while (true) {
            try {
                System.out.println("Введите порт");
                int port = Integer.parseInt(scanner.nextLine());
                Selector selector = Selector.open();
                try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
                    socketChannel.bind(new InetSocketAddress(port));
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    logger.debug("Сервер запущен.");
                    logger.debug("Подключение к БД");
                    CollectionManager.getManager().load(file);
                    logger.debug("Сервер ожидает подключения клиентов");
                    while (selector.isOpen()) {
                        int count = selector.select();
                        if (count == 0) {
                            continue;
                        }
                        Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                        while (iter.hasNext()) {
                            SelectionKey key = iter.next();
                            try {
                                if (key.isAcceptable()) {
                                    SocketChannel channel = socketChannel.accept();
                                    logger.debug("К серверу подключился клиент");
                                    channel.configureBlocking(false);
                                    channel.register(selector, SelectionKey.OP_READ);
                                }
                                if (key.isReadable()) {
                                    Future<Command> future = new ServerReceiver(key).receive();
                                    command = future.get();
                                    answer = new ServerHandler(command);
                                    Thread process = new Thread(answer);
                                    process.start();
                                    process.join();
                                }
                                if (key.isWritable()) {
                                    ExecutorService poolSend = Executors.newCachedThreadPool();
                                    ServerSender send = new ServerSender(key, answer);
                                    poolSend.execute(send);
                                    poolSend.shutdown();
                                    poolSend.awaitTermination(1, TimeUnit.MINUTES);
                                }
                                iter.remove();
                            } catch (IOException | ExecutionException e) {
                                logger.error("Клиент отключился");
                                key.cancel();
                            }
                        }
                    }
                }
            } catch (BindException e) {
                logger.error("Такой порт уже используется");
            } catch (NumberFormatException e) {
                logger.error("Порт не число или выходит за пределы");
            } catch (IllegalArgumentException e) {
                logger.error("Порт должен принимать значения от 1 до 65535");
            } catch (SocketException e) {
                logger.error("Недопустимый порт");
            }
        }
    }
}