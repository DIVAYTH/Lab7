package proga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ServerSender implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerSender.class);
    private SelectionKey key;
    private String answer;

    public ServerSender(SelectionKey key, String answer) {
        this.key = key;
        this.answer = answer;
    }

    /**
     * Метод отправляет результат выполнения команды, регистрации или авторизации клиенту
     */
    public void run() {
        SocketChannel channel = (SocketChannel) key.channel();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream toClient = new ObjectOutputStream(baos)) {
            toClient.writeObject(answer);
            ByteBuffer buffer = ByteBuffer.wrap(baos.toByteArray());
            int available = channel.write(buffer);
            while (available > 0) {
                available = channel.write(buffer);
            }
            buffer.clear();
            buffer.flip();
            logger.debug("Результат отправлен клиенту");
        } catch (IOException e) {
            // Все под контролем
        }
    }
}