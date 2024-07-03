import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegisterWindow extends JFrame {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public RegisterWindow() {
        this.setTitle("用户注册");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLayout(new GridLayout(3, 2));

        this.add(new JLabel("用户名:"));
        final JTextField usernameField = new JTextField();
        this.add(usernameField);

        this.add(new JLabel("密码:"));
        final JPasswordField passwordField = new JPasswordField();
        this.add(passwordField);

        JButton registerButton = new JButton("注册");
        this.add(registerButton);

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (registerUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "注册成功，请登录");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "注册失败，用户名可能已存在");
                }
            }
        });
    }

    private boolean registerUser(String username, String password) {
        String checkQuery = "SELECT * FROM Users WHERE username = ?";
        String insertQuery = "INSERT INTO Users (username, password) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            if (connection == null) {
                System.err.println("无法建立数据库连接以进行注册。");
                return false;
            }

            checkStatement.setString(1, username);
            ResultSet checkResultSet = checkStatement.executeQuery();

            if (checkResultSet.next()) {
                System.out.println("用户名已存在: " + username);
                return false;
            }

            String hashedPassword = passwordEncoder.encode(password);
            insertStatement.setString(1, username);
            insertStatement.setString(2, hashedPassword);
            int rowsInserted = insertStatement.executeUpdate();
            System.out.println("插入的行数: " + rowsInserted);
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("SQL 错误: " + e.getMessage());
            return false;
        }
    }
}
