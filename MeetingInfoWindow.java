import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MeetingInfoWindow extends JFrame {
    private int currentUserId;
    private DefaultListModel<String> meetingListModel;
    private JList<String> meetingList;

    public MeetingInfoWindow(int userId) {
        this.currentUserId = userId;
        setTitle("会议信息");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        meetingListModel = new DefaultListModel<>();
        meetingList = new JList<>(meetingListModel);
        JScrollPane scrollPane = new JScrollPane(meetingList);
        add(scrollPane, BorderLayout.CENTER);

        loadUserMeetingsFromDatabase();
    }

    private void loadUserMeetingsFromDatabase() {
        String query = "SELECT b.booking_id, m.title, m.description, b.start_time, b.end_time, r.room_name " +
                "FROM dbo.bookings b " +
                "JOIN dbo.meetings m ON b.room_id = m.id " +
                "JOIN dbo.rooms r ON b.room_id = r.room_id " +
                "WHERE b.user_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentUserId);
            ResultSet resultSet = statement.executeQuery();
            List<String> meetings = new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            while (resultSet.next()) {
                int bookingId = resultSet.getInt("booking_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String startTime = resultSet.getTimestamp("start_time").toLocalDateTime().format(formatter);
                String endTime = resultSet.getTimestamp("end_time").toLocalDateTime().format(formatter);
                String location = resultSet.getString("room_name");

                meetings.add(String.format("ID: %d, 标题: %s, 描述: %s, 开始时间: %s, 结束时间: %s, 地点: %s",
                        bookingId, title, description, startTime, endTime, location));
            }

            for (String meeting : meetings) {
                meetingListModel.addElement(meeting);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "无法加载会议信息: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
