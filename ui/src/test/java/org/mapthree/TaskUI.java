package org.mapthree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.Properties;

/**
 * Represents the user interface for interacting with tasks.
 */
public class TaskUI {
    private TaskList taskList;
    private final TaskStorage taskStorage;
    private final String filePath;
    private boolean exitRequested = false;

    /**
     * Constructs a TaskUI object.
     *
     * @throws IOException if an I/O error occurs during initialization
     */
    public TaskUI() throws IOException {
        taskStorage = new TaskStorage();
        filePath = findFilePath();
        System.out.println(filePath);
        try {
            taskList = taskStorage.load(filePath);
            System.out.println(taskList);
        } catch (IOException e) {
            System.out.println("in catch");
            e.printStackTrace();
            taskList = new TaskList();
        }
    }

    /**
     * Starts the task user interface.
     *
     * @throws IOException if an I/O error occurs during interaction
     */
    public void start() throws IOException, NoSuchFieldException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;

        while(!exitRequested) {
            System.out.println("Enter command: add <task> | done <index> | priority <priority> for <index> | list | exit");
            line = reader.readLine();

            String[] commandParts = splitLine(line);
            if(commandIsCorrect(commandParts))
                processCommand(commandParts);
            else
                System.out.println("Unknown command!");
        }
        taskStorage.save(filePath, taskList);
    }

    /**
     * Splits a line of input into an array of command parts.
     *
     * @param line the input line to be split
     * @return an array of command parts
     */
    private String[] splitLine(String line) {
        return line.split(" ", 2);
    }

    /**
     * Checks if a command is correct based on its parts.
     *
     * @param commandParts the parts of the command
     * @return true if the command is correct, false otherwise
     */
    private boolean commandIsCorrect(String[] commandParts) {
        if(commandParts.length == 0)
            return false;

        String keyWord = commandParts[0];
        if(commandParts.length == 1)
            return keyWord.equals("list") || keyWord.equals("exit");
        else
            return keyWord.equals("add") || keyWord.equals("done") || keyWord.equals("priority");
    }

    /**
     * Processes a command and performs the corresponding action.
     *
     * @param commandParts the parts of the command
     */
    private void processCommand(String[] commandParts) throws NoSuchFieldException {
        String command = commandParts[0];

        switch (command) {
            case "add" -> addTask(commandParts);
            case "done" -> markTaskAsDone(commandParts);
            case "priority" -> setPriority(commandParts);
            case "list" -> listTasks();
            case "exit" -> exitApplication();
        }
    }

    /**
     * Adds a task to the task list.
     *
     * @param parts the parts of the command
     */
    private void addTask(String[] parts) throws NoSuchFieldException {
        String taskDescription = parts[1];
        Task taskToAdd = new Task(taskDescription);
        if(taskToAdd.getDescription() != null)
            taskList.addTask(taskToAdd);
    }

    /**
     * Marks a task as done based on the index provided.
     *
     * @param parts the parts of the command
     */
    private void markTaskAsDone(String[] parts) {
        try {
            int index = Integer.parseInt(parts[1]) - 1;
            Optional<Task> taskOptional = taskList.getTask(index);
            Task task = taskOptional
                    .orElseThrow(() -> new IllegalArgumentException("Index is out of bounds!"));
            task.setDone(true);
        } catch (NumberFormatException e) {
            System.out.println("Invalid task index!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setPriority(String[] parts) {
        String[] splittedParts = parts[1].split("\\s+");

        try {
            int index = Integer.parseInt(splittedParts[2]) - 1;
            Optional<Task> taskOptional = taskList.getTask(index);
            Task task = taskOptional
                    .orElseThrow(() -> new IllegalArgumentException("Index is out of bounds!"));

            int priority = Integer.parseInt(splittedParts[0]);
            task.setPriority(priority);
        } catch (NumberFormatException e) {
            System.out.println("Invalid task index!");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lists all the tasks in the task list.
     */
    private void listTasks() {
        for (int i = 0; i < taskList.size(); i++) {
            Task task = taskList.getTask(i).get();
            int index = i + 1;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(index).append(": ").append(task.getDescription());

            int taskPriority = task.getPriority();
            if(taskPriority != 0)
                stringBuilder.append(" Pr").append(taskPriority);

            if(task.isDone())
                stringBuilder.append(" DONE");

            System.out.println(stringBuilder);
        }
    }

    /**
     * Exits the application.
     */
    private void exitApplication() {
        exitRequested = true;
    }

    /**
     * Finds the file path from the configuration properties.
     *
     * @return the file path
     * @throws IOException if an I/O error occurs during the file path retrieval
     */
    private static String findFilePath() throws IOException {
        Properties properties = new Properties();
        properties.load(TaskUI.class.getResourceAsStream("/config.properties"));
        return properties.getProperty("resource.file");
    }
}
