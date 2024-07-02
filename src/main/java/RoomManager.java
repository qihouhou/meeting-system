import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RoomManager {

    public void getAvailableRooms(LocalDateTime startTime, LocalDateTime endTime) {
        String query = "SELECT * FROM rooms WHERE room_id NOT IN (SELECT room_id FROM bookings WHERE start_time < ? AND end_time > ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, endTime);
            preparedStatement.setObject(2, startTime);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                System.out.println("Room ID: " + resultSet.getInt("room_id"));
                System.out.println("Room Name: " + resultSet.getString("room_name"));
                System.out.println("Capacity: " + resultSet.getInt("capacity"));
                System.out.println("Location: " + resultSet.getString("location"));
                System.out.println("-----");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
