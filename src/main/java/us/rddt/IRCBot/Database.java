package us.rddt.IRCBot;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper class to handle database transactions.
 * 
 * @author Ryan Morrison
 */
public class Database {
    /*
     * Class variables.
     */
    private Connection connection = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    private Statement statement = null;

    /**
     * Class constructor.
     */
    public Database() {
    }

    /**
     * Connects to the database as specified in IRCBot.properties
     * @throws SQLException if a SQL exception occurs
     * @throws ClassNotFoundException if the JDBC driver cannot be loaded
     * @throws IOException if the properties file cannot be loaded
     */
    public void connect() throws SQLException, ClassNotFoundException, IOException {
        if(Configuration.getDatabaseDriver().equalsIgnoreCase("mysql")) {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + Configuration.getMySQLServer() + "/" + Configuration.getMySQLDatabase() + "?user=" + Configuration.getMySQLUser() + "&password=" + Configuration.getMySQLPassword());
        } else if(Configuration.getDatabaseDriver().equalsIgnoreCase("sqlite")) {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + Configuration.getSQLiteDatabase() + ".db");
        } else {
            throw new SQLException("Invalid SQL configuration in properties file");
        }
        statement = connection.createStatement();
    }

    /**
     * Cleans up any potential connections left behind when the database was accessed
     * @throws SQLException if a SQL exception occurs
     */
    public void disconnect() throws SQLException {
        if(resultSet != null) resultSet.close();
        if(statement != null) statement.close();
        if(connection != null) connection.close();
    }

    /**
     * Gets the database connection
     * @return the database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Gets the prepared statement for use with the database
     * @return the prepared statement for use with the database
     */
    public PreparedStatement getPreparedStatement() {
        return preparedStatement;
    }

    /**
     * Gets the result set for a query, or null if the query has not executed
     * @return the result set for a processed query
     */
    public ResultSet getResultSet() {
        return resultSet;
    }

    /**
     * Gets the connection's statement
     * @return the connection's statement
     */
    public Statement getStatement() {
        return statement;
    }
}
