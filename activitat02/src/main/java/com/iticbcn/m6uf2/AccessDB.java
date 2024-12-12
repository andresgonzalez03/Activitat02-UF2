package com.iticbcn.m6uf2;

import java.sql.*;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*; 
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class AccessDB {
    private static final int PAGE_SIZE = 10;
    private static final String PATH = System.getProperty("user.home");

    public static void crearDirectori() throws IOException {
        File carpeta = new File(PATH + "/HorarisXML");
        if(!carpeta.exists()) {
            if(carpeta.mkdirs()) {
                System.out.println("Directori creat: " + carpeta.getPath());
            } else {
                System.out.println("No s'ha pogut fer el directori: " + carpeta.getPath());
            }
        } else {
            System.out.println("El directori ja existeix: " + carpeta.getPath());
        }
    }
    public static void createDatabase(String dbName) throws SQLException {
        try(Connection connection =  ConnDB.getConnection()) {
            String query = "create database if not exists " + dbName;
            try (Statement st = connection.createStatement()) {
                st.executeUpdate(query);
                System.out.println("Base de dades creada amb nom: " + dbName);
                updateConfigProperties(dbName);
            }
        }
    }
    private static void updateConfigProperties(String dbName) {
        String userDir = System.getProperty("user.dir");
        Path configPath = Paths.get(userDir, "Activitat02-UF2","activitat02","src", "main", "resources", "config.properties");
        Properties properties = new Properties();

        try (InputStream input = new FileInputStream(configPath.toFile())) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo cargar el archivo config.properties");
        }
        String newUrl = "jdbc:mariadb://localhost:3306/" + dbName;
        properties.setProperty("db.url", newUrl);
        try (OutputStream output = new FileOutputStream(configPath.toFile())) {
            properties.store(output, "Actualizado después de crear la base de datos");
            System.out.println("Archivo config.properties actualizado con la URL: " + newUrl);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("No se pudo guardar el archivo config.properties");
        }
    }
    public static void createTables(String dbName) {
        try (Connection conn = ConnDB.getConnection(dbName)) {
            String query1 = """
                    CREATE TABLE IF NOT EXISTS Estacio (
                        id INT PRIMARY KEY,
                        Nombre VARCHAR(255) NOT NULL
                    );
                    """;
            String query2 = """
                    CREATE TABLE IF NOT EXISTS Horari (
                        id INT PRIMARY KEY,
                        hora_Salida TIME NOT NULL,
                        hora_Llegada TIME NOT NULL,
                        fecha DATE NOT NULL
                    );
                    """;
            String query3 = """
                    CREATE TABLE IF NOT EXISTS Trajecte (
                        id INT PRIMARY KEY,
                        id_EstOrigen INT,
                        id_EsDestino INT,
                        id_Horari INT,
                        FOREIGN KEY (id_EstOrigen) REFERENCES Estacio(id),
                        FOREIGN KEY (id_EsDestino) REFERENCES Estacio(id),
                        FOREIGN KEY (id_Horari) REFERENCES Horari(id)
                    );
                    """;
            String query4 = """
                    CREATE TABLE IF NOT EXISTS Companyia (
                        id INT PRIMARY KEY,
                        Nombre VARCHAR(255) NOT NULL
                    );
                    """;
            String query5 = """
                    CREATE TABLE IF NOT EXISTS Tren (
                        id INT PRIMARY KEY,
                        Nombre VARCHAR(255) NOT NULL,
                        Capacitat INT NOT NULL
                    );
                    """;
            String query6 = """
                    CREATE TABLE IF NOT EXISTS Tenir (
                        idEstacio INT,
                        idTrajecte INT,
                        PRIMARY KEY (idEstacio, idTrajecte),
                        FOREIGN KEY (idEstacio) REFERENCES Estacio(id),
                        FOREIGN KEY (idTrajecte) REFERENCES Trajecte(id)
                    );
                    """;
            String query7 = """
                    CREATE TABLE IF NOT EXISTS Recorregut (
                        id INT PRIMARY KEY,
                        id_Trajecte INT,
                        id_Companyia INT,
                        id_Tren INT,
                        FOREIGN KEY (id_Trajecte) REFERENCES Trajecte(id),
                        FOREIGN KEY (id_Companyia) REFERENCES Companyia(id),
                        FOREIGN KEY (id_Tren) REFERENCES Tren(id)
                    );
                    """;
            try (Statement st = conn.createStatement()) {
                st.executeUpdate(query1);
                st.executeUpdate(query2);
                st.executeUpdate(query3);
                st.executeUpdate(query4);
                st.executeUpdate(query5);
                st.executeUpdate(query6);
                st.executeUpdate(query7);
                System.out.println("Taules creades correctament");
            }
        } catch (SQLException e) {
            System.err.println("Error al crear les taules: " + e.getMessage());
            e.printStackTrace();
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
    public static void mostraPagina(int page) {
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

    public static void writeXML(String dbName) throws Exception {
        crearDirectori();
        String ruta = PATH + "/HorarisXML/";
        String nomArxiu = ruta + "horarisGenerats.xml";
        try(Connection conn = ConnDB.getConnection(dbName)) {
            String query = "select * from Horari";
            try(PreparedStatement pst = conn.prepareStatement(query); ResultSet rs = pst.executeQuery()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                DOMImplementation implementation = builder.getDOMImplementation();
                Document document = implementation.createDocument(null, "encarrecs", null);
                document.setXmlVersion("1.0");

                Element arrel = document.getDocumentElement();
                while(rs.next()) {
                    Element horariElement = document.createElement("Horari");
                    crearElement("ID", String.valueOf(rs.getInt("id")), horariElement, document);
                    crearElement("HoraSortida", rs.getString("hora_Salida"), horariElement, document);
                    crearElement("HoraArribada", rs.getString("hora_Llegada"), horariElement, document);
                    crearElement("Data", rs.getString("fecha"), horariElement, document);
                    arrel.appendChild(horariElement);
                }
                Source source = new DOMSource(document);
                Result result = new StreamResult(new FileWriter(nomArxiu));
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes"); 
                transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "5");
                transformer.transform (source, result);
                System.out.println("XML generat correctament a la ruta: " + nomArxiu);
            }
        }
    }
    public static void crearElement(String dadaEmpleat, String valor, Element arrel, Document document) {
        Element element = document.createElement(dadaEmpleat);
        Text text = document.createTextNode(valor);
        element.appendChild(text);
        arrel.appendChild(element);
    }
    public static void mostraRegistrePerId(String dbName, String tableName, int id) {
        String query = "select * from " + tableName + " where id = ?";
        try(Connection conn = ConnDB.getConnection(dbName); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if(rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Hora Salida: " + rs.getTime("hora_Salida") + ", Hora Llegada: " + rs.getTime("hora_Llegada") + ", Fecha: " + rs.getDate("fecha"));
            } else {
                System.out.println("No s'ha trobat cap registre amb ID: " + id);
            }
        } catch(SQLException e) {
            System.err.println("Error a l'accedir a la base de dades: " + e.getMessage());
            e.printStackTrace();  
        }
    }
    public static void mostraRegistrePerCamp(String dbName, String tableName, String columnName, String pattern) {
        String query = "select * from " + tableName + " where " + columnName + " like ?";
        try(Connection conn = ConnDB.getConnection(dbName); PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, pattern);
            try(ResultSet rs = pst.executeQuery()) {
                boolean trobat = false;
                while(rs.next()) {
                    trobat = true;
                    System.out.println("ID: " + rs.getInt("id") + ", Hora Salida: " + rs.getTime("hora_Salida") + ", Hora Llegada: " + rs.getTime("hora_Llegada") + ", Fecha: " + rs.getDate("fecha"));
                }
                if(!trobat) {
                    System.out.println("No s'han trobat registres que coincideixin amb: " + pattern);
                }
            }
        } catch(SQLException e) {
            System.err.println("Error al accedir a la base de dades: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void modificaCamp(String dbName, String tableName, int id, String camp, String nouValor) {
        String query = "update " + tableName + "set " + camp + " = ? where id = ?";
        try(Connection conn = ConnDB.getConnection();) {
            boolean statusAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false); 
            try(PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setString(1, nouValor);
                pst.setInt(2, id);
                int filesAfectades = pst.executeUpdate();
                conn.commit();
                if (filesAfectades > 0) {
                    System.out.println("Registre actualitzat correctament");
                } else {
                    System.out.println("No s'ha trobat cap registre amb ID: " + id);
                }
            } catch(SQLException e) {
                conn.rollback();
                System.err.println("Error al fer les modificacions a la taula " + tableName + " " + e.getMessage());
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(statusAutoCommit);
            }
        } catch(SQLException e) {
            System.err.println("Error al accedir a la base de dades: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static void esborrarPerId(String dbName, String tableName, int id) {
        String query = "delete from " + tableName + " where id = ?";
        try(Connection conn = ConnDB.getConnection(dbName)){
            boolean statusAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try(PreparedStatement pst = conn.prepareStatement(query)) {
                pst.setInt(1, id);
                int filesAfectades = pst.executeUpdate();
                conn.commit();
                if (filesAfectades > 0) {
                    System.out.println("Registre amb ID " + id + " eliminat de la taula " + tableName);
                } else {
                    System.out.println("No s'ha trobat cap registre amb ID " + id + " a la taula " + tableName);
                }
            } catch(SQLException e) {
                conn.rollback();
                System.err.println("Error a l'esborrar a la taula " + tableName + " " + e.getMessage());
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(statusAutoCommit);
            }
        } catch(SQLException e) {
            System.err.println("Error al accedir a la base de dades: " + e.getMessage());
            e.printStackTrace();
        }
    }
}