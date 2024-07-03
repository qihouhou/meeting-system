import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonalInfoWindow extends JFrame {
    private int userId;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton saveButton;
    private JLabel userIdLabel;

    public PersonalInfoWindow(int userId) {
        this.userId = userId;

        setTitle("个人信息");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        userIdLabel = new JLabel("用户ID: " + userId);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(userIdLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++;
        add(new JLabel("用户名:"), gbc);

        gbc.gridx = 1;
        userNameField = new JTextField(15);
        add(userNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("密码:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        saveButton = new JButton("保存");
        add(saveButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUserInfo();
            }
        });

        loadUserInfo();
    }

    private void loadUserInfo() {
        String query = "SELECT username, password FROM dbo.Users WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            System.out.println("Executing query: " + preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userNameField.setText(resultSet.getString("username"));
                passwordField.setText(resultSet.getString("password"));
            } else {
                JOptionPane.showMessageDialog(this, "无法找到用户信息", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "加载用户信息失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveUserInfo() {
        String userName = userNameField.getText();
        String password = new String(passwordField.getPassword());
        String updateQuery = "UPDATE dbo.Users SET username = ?, password = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);
            preparedStatement.setInt(3, userId);

            System.out.println("Executing update: " + preparedStatement);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "用户信息已更新");
            } else {
                JOptionPane.showMessageDialog(this, "更新用户信息失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "保存用户信息失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PersonalInfoWindow(1).setVisible(true); // 示例：使用用户ID 1
            }
        });
    }
}
