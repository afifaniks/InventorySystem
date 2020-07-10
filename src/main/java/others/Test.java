package main.java.others;

import main.java.controllers.PromptDialogController;

import java.sql.*;

public class Test {
    private static final String URL = "jdbc:mysql://localhost:3306/testdb?autoReconnect=yes&useSSL=no";

    public static Connection getConnection() {
        Connection con;
        try {
            con = DriverManager.getConnection(URL, "root", "root");
        } catch (SQLException e) {
            e.printStackTrace();
            if (e.getErrorCode() == 0) { //Error Code 0: database server offline
                new PromptDialogController("Error!", "Database server is offline!");
            }
            return null;
        }
        return con;
    }

    public static void main(String[] args) {
        Connection connection = getConnection();
        try {
            PreparedStatement getCustomerList = connection.prepareStatement("SELECT * FROM customers");
            ResultSet resultSet = getCustomerList.executeQuery();

            System.out.println(resultSet.getString(2));

            while (resultSet.next()) {
                System.out.println(resultSet.getString(2));
            }

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
