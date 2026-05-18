package view;

import controller.TaskController;
import model.Task;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardView extends JFrame {

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;

    private TaskController taskController;
    private User currentUser;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public DashboardView(User user) {

        this.currentUser = user;
        this.taskController = new TaskController();

        setTitle("Dashboard - " + user.getName());
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ── TOP: welcome label ──
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(welcomeLabel, BorderLayout.NORTH);

        // ── CENTER: table ──
        String[] columns = {"ID", "Title", "Description", "Status", "Due Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        taskTable = new JTable(tableModel);

        // hide the ID column
        taskTable.getColumnModel().getColumn(0).setMinWidth(0);
        taskTable.getColumnModel().getColumn(0).setMaxWidth(0);

        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // ── BOTTOM: buttons ──
        JPanel bottomPanel = new JPanel(new FlowLayout());

        JButton addButton      = new JButton("Add");
        JButton editButton     = new JButton("Edit");
        JButton deleteButton   = new JButton("Delete");
        JButton markDoneButton = new JButton("Mark as Done");
        JButton logoutButton   = new JButton("Logout");
        JButton filterButton   = new JButton("Filter");
        filterCombo = new JComboBox<>(new String[]{"All", "TODO", "DONE"});

        bottomPanel.add(addButton);
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(markDoneButton);
        bottomPanel.add(new JLabel("Filter:"));
        bottomPanel.add(filterCombo);
        bottomPanel.add(filterButton);
        bottomPanel.add(logoutButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // ── ADD ──
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TaskFormView(currentUser, null, DashboardView.this);
            }
        });

        // ── EDIT ──
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = taskTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a task to edit.");
                    return;
                }
                Task selected = getTaskFromRow(row);
                new TaskFormView(currentUser, selected, DashboardView.this);
            }
        });

        // ── DELETE ──
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = taskTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a task to delete.");
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure?");
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    taskController.deleteTask(id);
                    loadTasks("All");
                    filterCombo.setSelectedIndex(0);
                }
            }
        });

        // ── MARK AS DONE ──
        markDoneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = taskTable.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a task.");
                    return;
                }
                Task selected = getTaskFromRow(row);
                selected.markAsCompleted();
                taskController.updateTask(selected);
                loadTasks("All");
                filterCombo.setSelectedIndex(0);
                JOptionPane.showMessageDialog(null, "Task marked as done!");
            }
        });

        // ── FILTER ──
        filterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filter = (String) filterCombo.getSelectedItem();
                loadTasks(filter);
            }
        });

        // ── LOGOUT ──
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginView();
            }
        });

        loadTasks("All");
        setVisible(true);
    }

    // fills the table - filter can be "All", "TODO", or "DONE"
    public void loadTasks(String filter) {

        System.out.println("Filter value: " + filter);
        tableModel.setRowCount(0);

        List<Task> tasks = taskController.getTasksByUser(currentUser.getId());
        if (tasks == null) return;

        for (Task task : tasks) {

            String status = task.getStatus();
            System.out.println("Task: " + task.getTitle() + " | Status: " + status);

            // apply filter
            if (filter.equals("DONE") && !status.equalsIgnoreCase("done")) continue;
            if (filter.equals("TODO") && status.equalsIgnoreCase("done")) continue;

            String dueDate = "";
            if (task.getDueDate() != null) {
                dueDate = task.getDueDate().format(formatter);
            }

            tableModel.addRow(new Object[]{
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    status,
                    dueDate
            });
        }
    }

    // builds a Task object from the selected row in the table
    private Task getTaskFromRow(int row) {

        Task task = new Task();
        task.setId((int) tableModel.getValueAt(row, 0));
        task.setTitle((String) tableModel.getValueAt(row, 1));
        task.setDescription((String) tableModel.getValueAt(row, 2));
        task.setStatus((String) tableModel.getValueAt(row, 3));
        task.setUser(currentUser);

        String dueDateText = (String) tableModel.getValueAt(row, 4);
        if (dueDateText != null && !dueDateText.isEmpty()) {
            task.setDueDate(LocalDateTime.parse(dueDateText, formatter));
        } else {
            task.setDueDate(LocalDateTime.now().plusDays(1));
        }

        return task;
    }
}