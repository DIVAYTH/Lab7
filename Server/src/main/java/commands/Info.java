package commands;

import proga.CollectionManager;

import java.util.Date;

public class Info extends AbstractCommand {
    private Date initDate = new Date();
    private CollectionManager manager;
    private String answer;

    public Info(CollectionManager manager) {
        this.manager = manager;
    }

    /**
     * Метод показывает информацию о коллекции
     *
     * @return
     */
    @Override
    public String execute() throws InterruptedException {
        Runnable info = () -> {
            synchronized (this) {
                answer = "Тип коллекции - PriorityQueue\n" +
                        "Дата инициализации " + initDate + "\n" +
                        "Размер коллекции " + manager.col.size();
                notify();
            }
        };
        new Thread(info).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}