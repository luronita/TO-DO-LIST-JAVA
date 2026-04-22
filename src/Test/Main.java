package Test;

import dao.UserDAO;
import dao.TaskDAO;
import model.User;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();
        TaskDAO taskDAO = new TaskDAO();

        // 🔹 1. CREATE USER
        User user = new User();
        user.setName("TestUser");
        user.setEmail("testuser@mail.com");
        user.setPassword("1234");

        boolean saved = userDAO.save(user);

        if (saved) {
            System.out.println("User saved successfully");
        } else {
            System.out.println("User save failed");
        }

        // 🔹 2. FETCH USER FROM DB
        User dbUser = userDAO.findByEmail("testuser@mail.com");

        if (dbUser == null) {
            System.out.println("User not found");
            return;
        }

        System.out.println("User found: " + dbUser.getName());

        // 🔹 3. CREATE TASK FOR THIS USER
        Task task = new Task();
        task.setTitle("Full test task");
        task.setDescription("Testing DAO flow");
        task.setCreationDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(2));
        task.setStatus("TODO");
        task.setUser(dbUser); // IMPORTANT

        taskDAO.save(task);
        System.out.println("Task saved");

        // 🔹 4. FETCH TASKS FOR USER
        List<Task> tasks = taskDAO.findByUser(dbUser.getId());

        System.out.println("Tasks for user:");

        for (Task t : tasks) {
            System.out.println(t.getTitle() + " | " + t.getStatus());
        }
    }
}