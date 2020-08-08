package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ServerReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ServerReceiver.class);
    private SelectionKey key;
    private Command command;

    public ServerReceiver(SelectionKey key) {
        this.key = key;
    }

    /**
     * Метод получает команду, логин или пароль от клиента
     *
     * @return
     */
    public Command receive() {
        try {
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
            }
            if (available == -1) {
                key.cancel();
            }
        } catch (IOException | ClassNotFoundException e) {
            // Все под контролем
        }
        return command;
    }
}