import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainWindow extends JFrame {
    private List<Meeting> meetings;
    private JTextArea meetingListTextArea;
    private JTextField titleField;
    private JTextField descriptionField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField locationField;
    private JTextField participantsField;
    private DefaultListModel<String> meetingListModel;
    private JList<String> meetingList;
    private int currentUserId;
    private DashboardWindow dashboardWindow;

    public MainWindow(int userId) {
        this.currentUserId = userId;
        this.dashboardWindow = new DashboardWindow(userId); // 假设DashboardWindow构造函数接受userId
        meetings = new ArrayList<>();

        setTitle("会议管理系统");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // First column labels
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("会议标题:"), gbc);

        gbc.gridy++;
        inputPanel.add(new JLabel("会议描述:"), gbc);

        gbc.gridy++;
        inputPanel.add(new JLabel("开始时间 (格式: yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridy++;
        inputPanel.add(new JLabel("结束时间 (格式: yyyy-MM-dd HH:mm):"), gbc);

        gbc.gridy++;
        inputPanel.add(new JLabel("会议地点:"), gbc);

        gbc.gridy++;
        inputPanel.add(new JLabel("参与者 (用逗号分隔):"), gbc);

        // Second column fields
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        titleField = new JTextField(20);
        inputPanel.add(titleField, gbc);

        gbc.gridy++;
        descriptionField = new JTextField(20);
        inputPanel.add(descriptionField, gbc);

        gbc.gridy++;
        startTimeField = new JTextField(20);
        inputPanel.add(startTimeField, gbc);

        gbc.gridy++;
        endTimeField = new JTextField(20);
        inputPanel.add(endTimeField, gbc);

        gbc.gridy++;
        locationField = new JTextField(20);
        inputPanel.add(locationField, gbc);

        gbc.gridx++;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JButton selectLocationButton = new JButton("选择会议地点");
        inputPanel.add(selectLocationButton, gbc);

        selectLocationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                LocationSelectionWindow locationSelectionWindow = new LocationSelectionWindow(MainWindow.this);
                locationSelectionWindow.setVisible(true);
                String selectedLocation = LocationSelectionWindow.getSelectedLocation();
                if (selectedLocation != null) {
                    locationField.setText(selectedLocation);
                }
            }
        });

        gbc.gridx = 1;
        gbc.gridy++;
        participantsField = new JTextField(20);
        gbc.gridwidth = 2;
        inputPanel.add(participantsField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addButton = new JButton("添加会议");
        buttonPanel.add(addButton);
        JButton removeButton = new JButton("删除会议");
        buttonPanel.add(removeButton);

        // 添加返回按钮
        JButton backButton = new JButton("返回");
        buttonPanel.add(backButton);

        inputPanel.add(buttonPanel, gbc);

        meetingListModel = new DefaultListModel<>();
        meetingList = new JList<>(meetingListModel);
        JScrollPane scrollPane = new JScrollPane(meetingList);

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

        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = meetingList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Meeting selectedMeeting = meetings.get(selectedIndex);
                    meetings.remove(selectedIndex);
                    try {
                        deleteMeetingFromDatabase(selectedMeeting.getId());
                        updateMeetingList();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(MainWindow.this, "删除会议失败: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // 关闭当前窗口
                dashboardWindow.setVisible(true); // 显示DashboardWindow
            }
        });

        loadMeetingsFromDatabase();
    }

    private void addMeeting() throws Exception {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String startTimeStr = startTimeField.getText();
        String endTimeStr = endTimeField.getText();
        String location = locationField.getText();
        List<String> participants = Arrays.asList(participantsField.getText().split(","));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
        LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);

        // Generate new meeting ID
        int bookingId = generateBookingId();
        Meeting meeting = new Meeting(bookingId, title, description, startTime, endTime, location, participants);
        meetings.add(meeting);
        addMeetingToDatabase(meeting, currentUserId);
        updateMeetingList();
    }

    private int generateBookingId() throws SQLException {
        String query = "SELECT NEXT VALUE FOR dbo.BookingSeq AS nextId";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            if (resultSet.next()) {
                return resultSet.getInt("nextId");
            } else {
                throw new SQLException("无法生成新的会议ID");
            }
        }
    }

    private void addMeetingToDatabase(Meeting meeting, int userId) throws SQLException {
        String query = "INSERT INTO bookings (booking_id, room_id, user_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, meeting.getId());
            preparedStatement.setInt(2, getRoomId(meeting.getLocation())); // 使用会议室名称获取 room_id
            preparedStatement.setInt(3, userId);
            preparedStatement.setObject(4, meeting.getStartTime());
            preparedStatement.setObject(5, meeting.getEndTime());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "会议已成功添加！");
            } else {
                throw new SQLException("会议添加失败");
            }
        }
    }

    private int getRoomId(String roomName) throws SQLException {
        String query = "SELECT room_id FROM rooms WHERE room_name = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, roomName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("room_id");
            } else {
                throw new SQLException("无法找到会议室: " + roomName);
            }
        }
    }

    private void deleteMeetingFromDatabase(int bookingId) throws SQLException {
        String query = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "会议已成功删除！");
            } else {
                throw new SQLException("会议删除失败");
            }
        }
    }

    private void loadMeetingsFromDatabase() {
        // Implement this method to load meetings from database
        // and add them to the meetings list and update the UI
    }

    private void updateMeetingList() {
        meetingListModel.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Meeting meeting : meetings) {
            meetingListModel.addElement(
                    "ID: " + meeting.getId() +
                            ", 标题: " + meeting.getTitle() +
                            ", 描述: " + meeting.getDescription() +
                            ", 开始时间: " + meeting.getStartTime().format(formatter) +
                            ", 结束时间: " + meeting.getEndTime().format(formatter) +
                            ", 地点: " + meeting.getLocation() +
                            ", 参与者: " + String.join(", ", meeting.getParticipants()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginWindow().setVisible(true);
            }
        });
    }
}
