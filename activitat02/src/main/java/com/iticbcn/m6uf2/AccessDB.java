package com.iticbcn.m6uf2;

import java.sql.*;
import java.io.*;
import java.util.Properties;

public class AccessDB {
    private static final int PAGE_SIZE = 10;

    public static void createDatabase(String dbName) throws SQLException {
        try(Connection connection =  ConnDB.getConnection()) {
            String query = "create database if not exists" + dbName;
            try (Statement st = connection.createStatement()) {
                st.executeQuery(query);
                System.out.println("Base de dades creada amb nom: " + dbName);
            }
            updateConfigProperties(dbName);
        }
    }
    public static void createTables(String dbName) {
        try(Connection conn = ConnDB.getConnection()) {
            String query = """
                        CREATE TABLE Estacio (
                            id INT PRIMARY KEY,
                            Nombre VARCHAR(255) NOT NULL
                        );
+
                        CREATE TABLE Horari (
                            id INT PRIMARY KEY,
                            hora_Salida TIME NOT NULL,
                            hora_Llegada TIME NOT NULL,
                            fecha DATE NOT NULL
                        );

                        CREATE TABLE Trajecte (
                            id INT PRIMARY KEY,
                            id_EstOrigen INT,
                            id_EsDestino INT,
                            id_Horari INT,
                            FOREIGN KEY (id_EstOrigen) REFERENCES Estacio(id),
                            FOREIGN KEY (id_EsDestino) REFERENCES Estacio(id),
                            FOREIGN KEY (id_Horari) REFERENCES Horari(id)
                        );
)
                        CREATE TABLE Companyia (
                            id INT PRIMARY KEY,
                            Nombre VARCHAR(255) NOT NULL
                        );

                        CREATE TABLE Tren (
                            id INT PRIMARY KEY,
                            Nombre VARCHAR(255) NOT NULL,
                            Capacitat INT NOT NULL
                        );

                        CREATE TABLE Tenir (
                            idEstacio INT,
                            idTrajecte INT,
                            PRIMARY KEY (idEstacio, idTrajecte),
                            FOREIGN KEY (idEstacio) REFERENCES Estacio(id),
                            FOREIGN KEY (idTrajecte) REFERENCES Trajecte(id)
                        );

                        CREATE TABLE Recorregut (
                            id INT PRIMARY KEY,
                            id_Trajecte INT,
                            id_Companyia INT,
                            id_Tren INT,
                            FOREIGN KEY (id_Trajecte) REFERENCES Trajecte(id),
                            FOREIGN KEY (id_Companyia) REFERENCES Companyia(id),
                            FOREIGN KEY (id_Tren) REFERENCES Tren(id)
                        );
                        """;
            try(Statement st = conn.createStatement()) {
                st.executeUpdate(query);
                System.out.println("Taules creades correctament");
            }
        } catch(SQLException e) {
            System.err.println("Error al crear les taules: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static void updateConfigProperties(String dbName) {
        String configFilePath = "src/main/resources/config.properties"; 
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(configFilePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar el archivo config.properties");
        }
        String newUrl = "jdbc:mariadb://localhost:3306/" + dbName;
        properties.setProperty("db.url", newUrl);
        try (OutputStream output = new FileOutputStream(configFilePath)) {
            properties.store(output, "Actualizado después de crear la base de datos");
            System.out.println("Archivo config.properties actualizado con la URL: " + newUrl);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo guardar el archivo config.properties");
        }
    }
    public static void insertHoraris(int id, String hora_Salida, String hora_Llegada, String fecha) {
        String query = "insert into Horari (id, hora_Salida, hora_Llegada, fecha) values (?, ?, ?, ?)";
        try(Connection conn = ConnDB.getConnection();) {
            boolean statusAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, id);
                pst.setTime(2, Time.valueOf(hora_Salida));
                pst.setTime(3, Time.valueOf(hora_Llegada));
                pst.setDate(4, Date.valueOf(fecha));
                pst.executeUpdate();
                conn.commit();
                System.out.println("Dades inserides correctament");
            } catch(SQLException e) {
                conn.rollback();    
                System.err.println("Error al fer les insercions a la taula Horari" + e.getMessage());
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(statusAutoCommit);
            }
        } catch(SQLException e) {
            System.err.println("Error a l'intentar conectar-se" + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void mostraPagina( int page) {
        int offset = (page - 1) * PAGE_SIZE;
        String query = "select * from Horari limit ? offset ?";
        try(Connection conn = ConnDB.getConnection(); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, PAGE_SIZE);
            pst.setInt(2, offset);
            ResultSet rs = pst.executeQuery();
            while(rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Hora Salida: " + rs.getTime("hora_Salida") + ", Hora Llegada: " + rs.getTime("hora_Llegada") + ", Fecha: " + rs.getDate("fecha"));
            }
        } catch(SQLException e) {
            System.err.println("Error a l'accedir a la base de dades: " + e.getMessage());
            e.printStackTrace();
        }
    }
}