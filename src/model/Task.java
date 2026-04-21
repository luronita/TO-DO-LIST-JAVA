package model;

import java.time.LocalDateTime;

public class Task {

    private int id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private LocalDateTime dueDate;
    private String status;
    private User user;

    public Task() {}
    
    public Task(int id, String title, String description,
                LocalDateTime creationDate, LocalDateTime dueDate,
                String status, User user) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.dueDate = dueDate;
        this.status = status;
        this.user = user;
    }

    public void markAsCompleted() {
        this.status = "DONE";
    }

    public void updateTask(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
