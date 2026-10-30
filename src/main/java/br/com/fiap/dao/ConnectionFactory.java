package br.com.fiap.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        // A conexão é fechada pelo try-with-resources no DAO.
        return DriverManager.getConnection(url, user, password);
    }
}
