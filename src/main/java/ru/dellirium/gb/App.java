package ru.dellirium.gb;

import java.sql.*;
import java.util.Scanner;

public class App {

    static Scanner scanner = new Scanner(System.in);
    private static Connection connection;
    private static Statement statement;

    public static void main( String[] args )
    {
        try {
            connect();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.execute("CREATE TABLE IF NOT EXISTS Goods (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "prodid INTEGER NOT NULL, " +
                    "title TEXT, " +
                    "cost INTEGER)");
            statement.execute("DELETE FROM Goods");

            connection.setAutoCommit(false);
            for (int i = 0; i < 10000; i++) {
                statement.executeUpdate("INSERT INTO Goods(prodid, title, cost)" +
                        "VALUES (" + i + ", 'товар" + i + "', " + i + "00)");
            }
            connection.setAutoCommit(true);

            while (true) {
                System.out.println("Команда: ");
                String command = scanner.nextLine();
                String[] pieces = command.split("\\s+");
                switch (pieces[0]) {
                    case "цена": {
                        ResultSet rs = statement.executeQuery("SELECT cost FROM Goods WHERE title = '" + pieces[1] + "'");
                        while (rs.next()) {
                            System.out.println(rs.getInt(1));
                        }

                        break;
                    }
                    case "сменитьцену":
                        statement.execute("UPDATE Goods SET cost = " + pieces[2] + " WHERE title = '" + pieces[1] + "'");
                        System.out.println("Цена успешно изменена");

                        break;
                    case "товарыпоцене": {
                        ResultSet rs = statement.executeQuery("SELECT * FROM Goods WHERE cost > " + pieces[1] + " AND cost < " + pieces[2] + "");
                        while (rs.next()) {
                            int id = rs.getInt("prodid");
                            String title = rs.getString("title");
                            int cost = rs.getInt("cost");
                            System.out.println(
                                    "id " + id + ", name " + title + ", cost " + cost
                            );
                        }
                        break;
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

/*        try {

        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:main.db");
        statement = connection.createStatement();
    }
}
