import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://127.0.0.1:1433;databaseName=MeetingManagementDB;integratedSecurity=true;encrypt=false";
    static {
        try {
            // 加载SQL Server JDBC驱动
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLServer JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(URL);
            System.out.println("Connection to SQL Server established using Windows Authentication.");
            return connection;
        } catch (SQLException e) {
            System.err.println("Failed to connect to SQL Server using Windows Authentication.");
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        getConnection();
    }
}
