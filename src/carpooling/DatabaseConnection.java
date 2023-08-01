package carpooling;
import java.sql.*;


//Define the DatabaseConnection class
class DatabaseConnection {
 private static final String DB_URL = "jdbc:mysql://localhost:3306/carpooling";
 private static final String USERNAME = "root";
 private static final String PASSWORD = "ajay1812";

 public static Connection getConnection() throws SQLException {
     return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
 }

 public static ResultSet executeQuery(String query) {
     try (Connection connection = getConnection();
          Statement statement = connection.createStatement()) {
         return statement.executeQuery(query);
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return null;
 }

    
   
}
