package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ServerReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ServerReceiver.class);
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private ObjectOutputStream toClient = new ObjectOutputStream(baos);
    private SelectionKey key;
    private Command command;
    private CollectionManager manager;
    private BDActivity bdActivity;

    public ServerReceiver(SelectionKey key) throws IOException {
        this.key = key;
        this.manager = manager;
        this.bdActivity = bdActivity;
    }

    /**
     * Метод получает команду от клиента
     */
    public Command receive() {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            SocketChannel channel = (SocketChannel) key.channel();
            int available = channel.read(buffer);
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
        } catch (IOException | ClassNotFoundException e) {
        }
        return command;
    }
}

