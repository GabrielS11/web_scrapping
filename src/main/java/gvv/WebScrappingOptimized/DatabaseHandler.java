package gvv.WebScrappingOptimized;

import gvv.Types.FlightOneWayData;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {


    private static Connection connection;
    private static final SessionFactory FACTORY = new Configuration()
            .configure("hibernate.cfg.xml")
            .buildSessionFactory();;

    public static void processOneWay(FlightOneWayData data){

    }


    public static void connect(){
        String url = "jdbc:mysql://localhost:3306/PDI_flight";
        String user = "root";
        String password = ""; // If no password, leave it empty
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;

    }

    public static void close() {
        try {
            if(connection != null && !connection.isClosed()){
                connection.commit();
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
