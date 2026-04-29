package Test;

import controller.AuthController;
import controller.TaskController;
import model.Task;
import model.User;
import view.LoginView;

import java.time.LocalDateTime;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        AuthController authController = new AuthController();

        boolean registered = authController.register(
                "TestUser2",
                "testuser2@gmail.com",
                "1234"
        );

        if (registered) {
            System.out.println("User registered successfully");
        } else {
            System.out.println("Registration failed");
        }

        User user = authController.login(
                "testuser2@gmail.com",
                "1234"
        );

        if (user != null) {
            System.out.println("Login successful: " + user.getName());
        } else {
            System.out.println("Login failed");
            return;
        }

        TaskController taskController = new TaskController();

        Task task = new Task();
        task.setTitle("Finish Java Project");
        task.setDescription("Complete MVC application");
        task.setCreationDate(LocalDateTime.now());
        task.setDueDate(LocalDateTime.now().plusDays(3));
        task.setStatus("TODO");
        task.setUser(user);

        boolean taskAdded = taskController.addTask(task);

        if (taskAdded) {
            System.out.println("Task added successfully");
        } else {
            System.out.println("Task creation failed");
        }

        List<Task> tasks = taskController.getTasksByUser(user.getId());

        System.out.println("Tasks for " + user.getName() + ":");

        for (Task t : tasks) {
            System.out.println(
                    t.getId() + " | " +
                            t.getTitle() + " | " +
                            t.getStatus()
            );
        }

        new LoginView();
    }
}