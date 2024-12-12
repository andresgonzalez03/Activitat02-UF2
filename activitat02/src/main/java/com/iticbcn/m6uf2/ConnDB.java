package com.iticbcn.m6uf2;

import java.io.InputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class ConnDB {

    public static Connection getConnection() throws SQLException {
        return getConnection(null);
    }
    public static Connection getConnection(String dbName) throws SQLException {
        Properties properties = new Properties();
        try (InputStream input = ConnDB.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new SQLException("No se pudo encontrar el archivo config.properties en resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new SQLException("No se pudo cargar el archivo de configuración", e);
        }
    
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
    
        if (url == null || username == null || password == null) {
            throw new SQLException("Faltan propiedades en el archivo de configuración.");
        }
        Connection connection = DriverManager.getConnection(url, username, password);
        if (dbName != null && !dbName.isEmpty()) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("USE " + dbName);
            }
        }
    
        return connection;
    }
    
}