package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

public class ServerReceiver implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerReceiver.class);
    private SelectionKey key;
    private CollectionManager manager;
    private BDActivity bdActivity;
    private ExecutorService poolSend;
    private ServerHandler serverHandler = new ServerHandler();

    public ServerReceiver(SelectionKey key, CollectionManager manager, BDActivity bdActivity, ExecutorService poolSend) {
        this.key = key;
        this.manager = manager;
        this.bdActivity = bdActivity;
        this.poolSend = poolSend;
    }

    /**
     * Метод получает команду или логин с паролем от клиента
     */
    @Override
    public void run() {
        try {
            Command command = null;
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            SocketChannel channel = (SocketChannel) key.channel();
            int available = channel.read(buffer);
            if (available > -1) {
                while (available > 0) {
                    available = channel.read(buffer);
                }
                byte[] buf = buffer.array();
                ObjectInputStream fromClient = new ObjectInputStream(new ByteArrayInputStream(buf));
                command = (Command) fromClient.readObject();
                fromClient.close();
                buffer.clear();
                if (command.getName().equals("reg") || command.getName().equals("sign")) {
                    logger.debug("От клиента получен логин и пароль");
                } else {
                    logger.info("От клиента получена команда " + command.getName());
                }
                serverHandler.handler(command, manager, bdActivity, poolSend, key);
            }
            if (available == -1) {
                key.cancel();
            }
        } catch (IOException | ClassNotFoundException e) {
            // Все под контролем
        }
    }
}