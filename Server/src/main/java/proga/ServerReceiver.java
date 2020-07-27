package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ServerReceiver {
    private static final Logger logger = LoggerFactory.getLogger(ServerReceiver.class);
    private SelectionKey key;
    private Command command;
    private ExecutorService executor = Executors.newFixedThreadPool(10);

    public ServerReceiver(SelectionKey key) {
        this.key = key;
    }

    /**
     * Метод получает команду от клиента
     *
     * @return
     */
    public Future<Command> receive() {
        return executor.submit(() -> {
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
            key.interestOps(SelectionKey.OP_WRITE);
            return command;
        });
    }
}
