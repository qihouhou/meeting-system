import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
public class LoginWindow extends JFrame {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public LoginWindow() {
        this.setTitle("用户登录");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(3, 2));

        this.add(new JLabel("用户名:"));
        final JTextField usernameField = new JTextField();
        this.add(usernameField);

        this.add(new JLabel("密码:"));
        final JPasswordField passwordField = new JPasswordField();
        this.add(passwordField);

        JButton loginButton = new JButton("登录");
        JButton registerButton = new JButton("注册");
        this.add(loginButton);
        this.add(registerButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                if (authenticateUser(username, password)) {
                    JOptionPane.showMessageDialog(null, "登录成功");
                    new MainWindow().setVisible(true);
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
        String query = "SELECT password FROM Users WHERE username = ?";
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
                return passwordEncoder.matches(password, storedHashedPassword);
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //public static void main(String[] args) {
        //SwingUtilities.invokeLater(new Runnable() {
            //public void run() {
                //new LoginWindow().setVisible(true);
            //}
        //});
        public static void main(String[] args) {
            RoomManager roomManager = new RoomManager();
            BookingManager bookingManager = new BookingManager();
            RoomResourceManager roomResourceManager = new RoomResourceManager();

            // 添加会议室
            roomResourceManager.addRoom(1, "Conference Room A", 10, "First Floor");
            roomResourceManager.addRoom(2, "Conference Room B", 20, "Second Floor");

            // 查询可用会议室
            roomManager.getAvailableRooms(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));

            // 预订会议室
            bookingManager.bookRoom(1, 1, 1, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

            // 更新会议室
            roomResourceManager.updateRoom(1, "Updated Conference Room A", 15, "First Floor");

            // 删除会议室
            roomResourceManager.deleteRoom(2);
        }
    }




