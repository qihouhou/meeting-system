import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BookingManager {

    public void bookRoom(int bookingId, int roomId, int userId, LocalDateTime startTime, LocalDateTime endTime) {
        String query = "INSERT INTO bookings (booking_id, room_id, user_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, bookingId);
            preparedStatement.setInt(2, roomId);
            preparedStatement.setInt(3, userId);
            preparedStatement.setObject(4, startTime);
            preparedStatement.setObject(5, endTime);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Booking successful!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
