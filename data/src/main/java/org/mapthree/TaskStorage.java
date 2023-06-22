package org.mapthree;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Handles the loading and saving of TaskList objects using JSON serialization.
 */
public class TaskStorage {
    private final Gson gson;

    /**
     * Constructs a TaskStorage object.
     */
    public TaskStorage() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Loads a TaskList object from a JSON file.
     *
     * @param fileName the name of the JSON file
     * @return the loaded TaskList object, or a new TaskList if the file doesn't exist or is empty
     * @throws IOException if an I/O error occurs during the loading process
     */
    public TaskList load(String fileName) throws IOException {
        Type taskListType = new TypeToken<TaskList>() {}.getType();
        try (FileReader reader = new FileReader(fileName)) {
            TaskList taskList = gson.fromJson(reader, taskListType);
            return taskList != null ? taskList : new TaskList();
        }
    }

    /**
     * Saves a TaskList object to a JSON file.
     *
     * @param fileName the name of the JSON file
     * @param taskList the TaskList object to be saved
     * @throws IOException if an I/O error occurs during the saving process
     */
    public void save(String fileName, TaskList taskList) throws IOException {
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(taskList, writer);
        }
    }
}
