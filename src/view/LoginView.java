package view;

import controller.AuthController;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    private AuthController authController;

    public LoginView() {

        authController = new AuthController();

        setTitle("Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout
        setLayout(new GridLayout(4, 2, 10, 10));

        // Components
        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        add(loginButton);
        add(registerButton);

        // Action Login
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                User user = authController.login(email, password);

                if (user != null) {
                    JOptionPane.showMessageDialog(null, "Login success: " + user.getName());

                    // ouvrir dashboard
                    //new DashboardView(user);
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(null, "Invalid email or password");
                }
            }
        });

        // Action Register
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //new RegisterView();
            }
        });

        setVisible(true);
    }
}