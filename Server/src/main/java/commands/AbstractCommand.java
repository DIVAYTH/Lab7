package commands;

import collectionClasses.StudyGroup;

import java.nio.channels.SelectionKey;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

/**
 * Класс наследник для всех команд
 */
public class AbstractCommand {

    public void executeCommand(ExecutorService poolSend, SelectionKey key) throws InterruptedException {
    }

    public void executeCommand(ExecutorService poolSend, SelectionKey key, String login) throws InterruptedException {
    }

    public void executeCommand(ExecutorService poolSend, SelectionKey key, String str, String login) throws InterruptedException {
    }

    public void executeCommand(ExecutorService poolSend, SelectionKey key, StudyGroup studyGroup, String login) throws InterruptedException, ExecutionException {
    }

    public void executeCommand(ExecutorService poolSend, SelectionKey key, String str, StudyGroup studyGroup, String login) throws InterruptedException {
    }
}