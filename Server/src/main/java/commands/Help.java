package commands;

import java.util.concurrent.Callable;

public class Help extends AbstractCommand {
    private String answer;

    /**
     * Метод выводит все доступные команды
     *
     * @return
     */
    @Override
    public String execute() throws InterruptedException {
        Runnable help = () -> {
            synchronized (this) {
                answer = "help - вывести справку по доступным командам\n" +
                        "info - вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)\n" +
                        "show - вывести в стандартный поток вывода все элементы коллекции в строковом представлении\n" +
                        "add - добавить новый элемент в коллекцию\n" +
                        "update id - обновить значение элемента коллекции, id которого равен заданному\n" +
                        "remove_by_id id - удалить элемент из коллекции по его id\n" +
                        "clear - очистить коллекцию\n" +
                        "execute_script file_name - считать и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме\n" +
                        "exit - завершить программу (без сохранения в файл)\n" +
                        "add_if_max - добавить новый элемент в коллекцию, если его значение height превышает значение height наибольшего элемента этой коллекции\n" +
                        "add_if_min - добавить новый элемент в коллекцию, если его значение height меньше, чем height у наименьшего элемента этой коллекции\n" +
                        "remove_greater height - удалить из коллекции все элементы, превышающие заданный\n" +
                        "remove_any_by_students_count studentsCount - удалить из коллекции один элемент, значение поля studentsCount которого эквивалентно заданному\n" +
                        "print_field_ascending_students_count - вывести значения поля studentsCount в порядке возрастания\n" +
                        "print_field_descending_form_of_education - вывести значения поля formOfEducation в порядке убывания";
                notify();
            }
        };
        new Thread(help).start();
        synchronized (this) {
            wait();
        }
        return answer;
    }
}