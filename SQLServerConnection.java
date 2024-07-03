import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerConnection {
    public static void main(String[] args) {
        // 数据库连接参数
        String url = "jdbc:sqlserver://115.25.73.122:1433;databaseName=library;integratedSecurity=true;encrypt=false";

        // 尝试连接数据库
        try {
            // 加载数据库驱动程序
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            // 获取数据库连接
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                System.out.println("连接成功！");
                // 在这里可以执行其他数据库操作
                conn.close(); // 记得在使用完连接后关闭
            }
        } catch (ClassNotFoundException e) {
            System.out.println("找不到数据库驱动程序类！");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("连接数据库时出错！");
            e.printStackTrace();
        }
    }
}

