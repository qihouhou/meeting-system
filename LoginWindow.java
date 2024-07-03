import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class LoginWindow extends JFrame {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static int currentUserId;

    public LoginWindow() {
        setTitle("用户登录");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("用户名:"));
        final JTextField usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("密码:"));
        final JPasswordField passwordField = new JPasswordField();
        add(passwordField);

        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "登录成功");
                    new DashboardWindow(currentUserId).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "用户名或密码错误");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 打开注册窗口
                new RegisterWindow().setVisible(true);
            }
        });
    }

    private boolean authenticateUser(String username, String password) {
        String query = "SELECT id, password FROM Users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (connection == null) {
                System.err.println("无法建立数据库连接以进行身份验证。");
                return false;
            }

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");
                currentUserId = resultSet.getInt("id");
                return passwordEncoder.matches(password, storedHashedPassword);
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}
