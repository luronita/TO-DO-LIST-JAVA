package controller;

import dao.TaskDAO;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;

public class TaskController {

    private TaskDAO taskDAO;

    public TaskController() {
        this.taskDAO = new TaskDAO();
    }

    public boolean addTask(Task task) {

        if (task == null) return false;

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) return false;

        if (task.getUser() == null) return false;

        if (task.getCreationDate() == null) {
            task.setCreationDate(LocalDateTime.now());
        }

        if (task.getDueDate() == null) {
            task.setDueDate(LocalDateTime.now().plusDays(1));
        }

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("TODO");
        }

        try {
            taskDAO.save(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateTask(Task task) {

        if (task == null || task.getId() == 0) return false;

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) return false;

        try {
            taskDAO.update(task);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean deleteTask(int id) {

        if (id <= 0) return false;

        try {
            taskDAO.delete(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Task> getTasksByUser(int userId) {

        if (userId <= 0) return null;

        try {
            return taskDAO.findByUser(userId);
        } catch (Exception e) {
            return null;
        }
    }
}