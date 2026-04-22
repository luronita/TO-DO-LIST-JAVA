package Test;

import util.DBConnection;
import java.sql.Connection;

public class TestDB {
    public static void main(String[] args) {


        try {
            Connection conn = DBConnection.getConnection();

            if (conn != null) {
                System.out.println("Connected to database.");
            } else {
                System.out.println("Connection failed.");
            }

        } catch (Exception e) {
            System.out.println("Error occurred:");
            e.printStackTrace();
        }
    }
}