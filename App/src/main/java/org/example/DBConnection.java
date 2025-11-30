package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/COMP3005_Final_Project"; // Edit if you use different port number/DB name
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to PostgreSQL successfully!");
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
        return conn;
    }
}



