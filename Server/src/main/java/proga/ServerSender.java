package proga;

import commands.AbstractCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class ServerSender implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerSender.class);
    private SelectionKey key;
    private ServerHandler answer;

    public ServerSender(SelectionKey key, ServerHandler answer) {
        this.key = key;
        this.answer = answer;
    }

    /**
     * Метод отправляет результат выполнения команды клиенту
     */
    @Override
    public void run() {
        try {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.wrap(answer.getBaos().toByteArray());
            int available = channel.write(buffer);
            while (available > 0) {
                available = channel.write(buffer);
            }
            buffer.clear();
            buffer.flip();
            key.interestOps(SelectionKey.OP_READ);
            logger.debug("Результат отправлен клиенту");
        } catch (IOException e) {
        }
    }
}