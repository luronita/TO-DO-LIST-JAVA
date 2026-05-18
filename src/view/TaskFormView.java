package view;

import controller.TaskController;
import model.Task;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskFormView extends JFrame {

    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField dueDateField;
    private JComboBox<String> statusCombo;
    private JButton saveButton;
    private JButton cancelButton;

    private TaskController taskController;
    private User currentUser;
    private Task existingTask; // null if adding, not null if editing
    private DashboardView dashboard;

    public TaskFormView(User user, Task task, DashboardView dashboard) {

        this.currentUser = user;
        this.existingTask = task;
        this.dashboard = dashboard;
        this.taskController = new TaskController();

        // if task is null we are ADDING, otherwise we are EDITING
        boolean isEditing = (task != null);

        setTitle(isEditing ? "Edit Task" : "Add Task");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridLayout(5, 2, 10, 10));

        add(new JLabel("Title:"));
        titleField = new JTextField();
        add(titleField);

        add(new JLabel("Description:"));
        descriptionField = new JTextField();
        add(descriptionField);

        add(new JLabel("Due Date (yyyy-MM-dd HH:mm):"));
        dueDateField = new JTextField();
        add(dueDateField);

        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(new String[]{"TODO", "DONE"});
        add(statusCombo);

        saveButton   = new JButton("Save");
        cancelButton = new JButton("Cancel");
        add(saveButton);
        add(cancelButton);

        // if editing, fill the fields with the existing task data
        if (isEditing) {
            titleField.setText(task.getTitle());
            descriptionField.setText(task.getDescription());
            if (task.getDueDate() != null) {
                dueDateField.setText(task.getDueDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            }
            statusCombo.setSelectedItem(task.getStatus());
        }

        // SAVE button
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String title = titleField.getText().trim();
                String description = descriptionField.getText().trim();
                String dueDateText = dueDateField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();

                // check title is not empty
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Title cannot be empty.");
                    return;
                }

                // parse the due date
                LocalDateTime dueDate;
                try {
                    dueDate = LocalDateTime.parse(dueDateText, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Invalid date format. Use: yyyy-MM-dd HH:mm\nExample: 2026-12-31 23:59");
                    return;
                }

                if (isEditing) {
                    // update the existing task
                    existingTask.setTitle(title);
                    existingTask.setDescription(description);
                    existingTask.setDueDate(dueDate);
                    existingTask.setStatus(status);
                    taskController.updateTask(existingTask);
                    JOptionPane.showMessageDialog(null, "Task updated successfully!");
                } else {
                    // create a new task
                    Task newTask = new Task();
                    newTask.setTitle(title);
                    newTask.setDescription(description);
                    newTask.setCreationDate(LocalDateTime.now());
                    newTask.setDueDate(dueDate);
                    newTask.setStatus(status);
                    newTask.setUser(currentUser);
                    taskController.addTask(newTask);
                    JOptionPane.showMessageDialog(null, "Task added successfully!");
                }

                // refresh the dashboard table
                dashboard.loadTasks("All");
                dispose(); // close this form
            }
        });

        // CANCEL button - just close the form
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }
}
