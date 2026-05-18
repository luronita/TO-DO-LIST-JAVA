package view;

import controller.AuthController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterView extends JFrame {

    // Step 1: declare the fields (the input boxes the user will type in)
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;

    private AuthController authController;

    public RegisterView() {

        // Step 2: create the authController so we can use register()
        authController = new AuthController();

        // Step 3: basic window setup (same as LoginView)
        setTitle("Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Step 4: GridLayout means the window is divided into rows and columns
        // 5 rows, 2 columns (one for the label, one for the input box)
        setLayout(new GridLayout(5, 2, 10, 10));

        // Step 5: add each label + input field pair
        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        add(confirmPasswordField);

        // Step 6: add the two buttons
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        add(registerButton);
        add(backButton);

        // Step 7: what happens when the user clicks "Register"
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // get what the user typed
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                // check that no field is empty
                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                    return; // stop here, don't continue
                }

                // check that both passwords match
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(null, "Passwords do not match.");
                    return; // stop here, don't continue
                }

                // try to register using the controller
                boolean success = authController.register(name, email, password);

                if (success) {
                    JOptionPane.showMessageDialog(null, "Account created! You can now login.");
                    dispose(); // close this window
                    new LoginView(); // go back to login
                } else {
                    JOptionPane.showMessageDialog(null, "Email already in use. Try another one.");
                }
            }
        });

        // Step 8: what happens when the user clicks "Back to Login"
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // close this window
                new LoginView(); // open the login window
            }
        });

        // Step 9: make the window visible
        setVisible(true);
    }
}