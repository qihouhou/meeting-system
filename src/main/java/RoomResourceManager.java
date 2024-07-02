import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RoomResourceManager {

    public void addRoom(int roomId, String roomName, int capacity, String location) {
        String query = "INSERT INTO rooms (room_id, room_name, capacity, location) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, roomId);
            preparedStatement.setString(2, roomName);
            preparedStatement.setInt(3, capacity);
            preparedStatement.setString(4, location);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Room added successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRoom(int roomId, String roomName, int capacity, String location) {
        String query = "UPDATE rooms SET room_name = ?, capacity = ?, location = ? WHERE room_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, roomName);
            preparedStatement.setInt(2, capacity);
            preparedStatement.setString(3, location);
            preparedStatement.setInt(4, roomId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Room updated successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, roomId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Room deleted successfully!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

