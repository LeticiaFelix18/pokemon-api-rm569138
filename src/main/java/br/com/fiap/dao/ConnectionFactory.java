package br.com.fiap.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private ConnectionFactory() { }

    public static Connection getConnection() throws SQLException {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");
        if (url == null || url.isBlank() || user == null || user.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalStateException("Variáveis de ambiente do banco não configuradas!");
        }
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver Oracle não encontrado.", e);
        }
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("oracle.net.CONNECT_TIMEOUT", "10000");
        properties.setProperty("oracle.jdbc.ReadTimeout", "15000");
        // Cada operação fecha sua conexão com try-with-resources no DAO.
        return DriverManager.getConnection(url, properties);
    }
}
