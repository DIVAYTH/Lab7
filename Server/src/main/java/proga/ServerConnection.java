package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.*;

public class ServerConnection {
    private static final Logger logger = LoggerFactory.getLogger(ServerConnection.class);
    private CollectionManager manager = new CollectionManager();
    private BDActivity bdActivity = new BDActivity();
    private Scanner scanner = new Scanner(System.in);
    private ExecutorService poolSend = Executors.newCachedThreadPool();
    private ExecutorService poolReceiver = Executors.newFixedThreadPool(2);

    /**
     * Метод реализует соединение и работу с клиентом
     *
     * @throws IOException
     */
    public void connection(String file) throws IOException, ClassNotFoundException, InterruptedException {
        logger.debug("Сервер запущен.");
        while (true) {
            try {
                System.out.println("Введите порт");
                int port = Integer.parseInt(scanner.nextLine());
                Selector selector = Selector.open();
                try (ServerSocketChannel socketChannel = ServerSocketChannel.open()) {
                    socketChannel.bind(new InetSocketAddress(port));
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_ACCEPT);
                    manager.loadCommands(manager, bdActivity);
                    String infCol = manager.loadToCol(file, bdActivity);
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
                                    channel.register(selector, SelectionKey.OP_WRITE);
                                }
                                if (key.isWritable()) {
                                    if (infCol == null) {
                                        poolSend.execute(new ServerSender(key, "Загружена коллекция размером " + manager.col.size()));
                                        key.interestOps(SelectionKey.OP_READ);
                                    } else {
                                        poolSend.execute(new ServerSender(key, infCol));
                                        Thread.sleep(1000);
                                        System.exit(1);
                                    }
                                }
                                if (key.isReadable()) {
                                    poolReceiver.submit(new ServerReceiver(key, manager, bdActivity, poolSend));
                                    key.interestOps(SelectionKey.OP_READ);
                                }
                                iter.remove();
                            } catch (CancelledKeyException e) {
                                logger.error("Клиент отключился");
                                logger.debug("Сервер ожидает подключения клиентов");
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
