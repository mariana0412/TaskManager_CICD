package org.mapthree;

import lombok.Data;
import org.mapthree.annotation.Author;
import org.mapthree.annotation.MaxStringLength;
import org.mapthree.annotation.Priority;

/**
 * Represents a task with a description and completion status.
 */
@Data
@Author(name = "Mariana")
public class Task{
    @MaxStringLength(value = 50)
    private String description;
    private boolean done;
    @Priority(min = 1)
    private int priority;

    /**
     * Constructs a new Task object with the given description.
     *
     * @param description the description of the task
     */
    public Task(String description) throws NoSuchFieldException {
        try {
            setDescription(description);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setDescription(String description) throws NoSuchFieldException {
        MaxStringLength maxStringLengthAnnotation = getClass()
                .getDeclaredField("description")
                .getAnnotation(MaxStringLength.class);
        int maxDescriptionLength = maxStringLengthAnnotation.value();

        if (description.length() > maxDescriptionLength)
            throw new IllegalArgumentException("Description exceeds the maximum length of " + maxDescriptionLength);
        else
            this.description = description;

    }

    public void setPriority(int priority) throws NoSuchFieldException {
        Priority priorityAnnotation = getClass()
                .getDeclaredField("priority")
                .getAnnotation(Priority.class);
        int minPriority = priorityAnnotation.min();

        if(priority < minPriority)
            throw new IllegalArgumentException("Priority must be greater than 0");
        else
            this.priority = priority;
    }

}
