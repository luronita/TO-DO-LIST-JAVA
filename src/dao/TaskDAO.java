package dao;

import model.Task;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public void save(Task task) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO tasks (title, description, creation_date, due_date, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(task.getCreationDate()));
            ps.setTimestamp(4, Timestamp.valueOf(task.getDueDate()));
            ps.setString(5, task.getStatus());
            ps.setInt(6, task.getUser().getId());

            ps.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void update(Task task) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "UPDATE tasks SET title = ?, description = ?, due_date = ?, status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, task.getTitle());
            ps.setString(2, task.getDescription());
            ps.setTimestamp(3, Timestamp.valueOf(task.getDueDate()));
            ps.setString(4, task.getStatus());
            ps.setInt(5, task.getId());

            ps.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "DELETE FROM tasks WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);

            ps.executeUpdate();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Task> findByUser(int userId) {
        List<Task> tasks = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM tasks WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Task task = new Task();

                task.setId(rs.getInt("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setCreationDate(rs.getTimestamp("creation_date").toLocalDateTime());
                task.setDueDate(rs.getTimestamp("due_date").toLocalDateTime());
                task.setStatus(rs.getString("status"));

                tasks.add(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasks;
    }
}