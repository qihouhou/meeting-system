import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainWindow extends JFrame {
    private List<Meeting> meetings;
    private JTextArea meetingListTextArea;
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField userIdField;
    private JTextField roomIdField;

    public MainWindow() {
        meetings = new ArrayList<>();

        setTitle("会议管理系统");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(7, 2));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("会议标题:"));
        titleField = new JTextField();
        inputPanel.add(titleField);

        inputPanel.add(new JLabel("会议描述:"));
        descriptionField = new JTextField();
        inputPanel.add(descriptionField);

        inputPanel.add(new JLabel("开始时间 (格式: yyyy-MM-dd HH:mm):"));
        startTimeField = new JTextField();
        inputPanel.add(startTimeField);

        inputPanel.add(new JLabel("结束时间 (格式: yyyy-MM-dd HH:mm):"));
        endTimeField = new JTextField();
        inputPanel.add(endTimeField);

        inputPanel.add(new JLabel("用户ID:"));
        userIdField = new JTextField();
        inputPanel.add(userIdField);

        inputPanel.add(new JLabel("会议室ID:"));
        roomIdField = new JTextField();
        inputPanel.add(roomIdField);

        JButton addButton = new JButton("添加会议");
        inputPanel.add(addButton);

        meetingListTextArea = new JTextArea();
        meetingListTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(meetingListTextArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addMeeting();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainWindow.this, "添加会议失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private void addMeeting() throws Exception {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        int userId = Integer.parseInt(userIdField.getText());
        int roomId = Integer.parseInt(roomIdField.getText());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

        Meeting meeting = new Meeting(title, description, startTime, endTime);
        meetings.add(meeting);
        addMeetingToDatabase(meeting, userId, roomId);
        updateMeetingList();
    }

    private void addMeetingToDatabase(Meeting meeting, int userId, int roomId) throws SQLException {
        String query = "INSERT INTO bookings (booking_id, room_id, user_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int bookingId = generateRandomBookingId();

            // 设置参数
            preparedStatement.setInt(1, bookingId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setObject(4, meeting.getStartTime());
            preparedStatement.setObject(5, meeting.getEndTime());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "会议已成功添加到数据库中！");
            } else {
                throw new SQLException("会议添加失败");
            }
        }
    }

    private void updateMeetingList() {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Meeting meeting : meetings) {
            sb.append("会议: ").append(meeting.getTitle()).append("\n")
                    .append("描述: ").append(meeting.getDescription()).append("\n")
                    .append("开始时间: ").append(meeting.getStartTime().format(formatter)).append("\n")
                    .append("结束时间: ").append(meeting.getEndTime().format(formatter)).append("\n\n");
        }
        meetingListTextArea.setText(sb.toString());
    }

    private static int generateRandomBookingId() {
        Random random = new Random();
        return random.nextInt(100000); // 假设生成 0 到 99999 之间的随机整数作为 booking_id
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow().setVisible(true));
    }
}
