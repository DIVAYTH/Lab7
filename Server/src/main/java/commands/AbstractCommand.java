package commands;

import collectionClasses.StudyGroup;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * Класс наследник для всех команд
 */
public class AbstractCommand {

    public String execute() throws InterruptedException {
        return null;
    }

    public String execute(String login) throws InterruptedException {
        return null;
    }

    public String execute(String str, String login) throws InterruptedException {
        return null;
    }

    public String execute(StudyGroup studyGroup, String login) throws InterruptedException, ExecutionException {
        return null;
    }

    public String execute(String str, StudyGroup studyGroup, String login) throws InterruptedException {
        return null;
    }

    public StudyGroup execute(String str1, String str2, String str3, String str4, String str5, String str6, String str7,
                              String str8, String str9, String str10, String str11, String str12, String str13) {
        return null;
    }

    public String execute(File file, String login) throws InterruptedException, FileNotFoundException {
        return null;
    }
}