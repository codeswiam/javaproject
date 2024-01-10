package gui;

import db.DBManager;
import gui.visualization.Colors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Authentification extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Authentification() {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setSize(450, 400);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.blue);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel("Job Portal Scraper");
        title.setFont(new Font("Futura", Font.BOLD, 40));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(50, 75, 50, 75));
        main.setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.CENTER);

        //
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.setBackground(Colors.white);

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Futura", Font.BOLD, 15));
        // usernameLabel.setHorizontalAlignment(0);
        usernameLabel.setForeground(Colors.blue);

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Futura", Font.BOLD, 15));
        //passwordLabel.setHorizontalAlignment(0);
        passwordLabel.setForeground(Colors.blue);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Montseratt", Font.BOLD, 15));
        loginButton.setForeground(Colors.blue);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                if (authenticateUser(username, password)) {
                    new JobPortalScraperUI();

                } else {
                    JOptionPane.showMessageDialog(Authentification.this, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(new JLabel()); // Empty label for spacing
        panel.add(loginButton);

        main.add(panel);

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.blue);
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private boolean authenticateUser(String username, String password) {
        /*String jdbcUrl = "jdbc:mysql://localhost:3306/jobportalscraper";
        String dbUser = "root";
        String dbPassword = "root";

         */

        DBManager dbManager = DBManager.getInstance();
        PreparedStatement selectStatement = null;
        PreparedStatement insertStatement = null;

        try (Connection connection = dbManager.makeConnection()) {
            String query = "SELECT * FROM user WHERE UserName = ? AND Password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // If there is a match, the user exists
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Authentification();
            }
        });
    }
}
