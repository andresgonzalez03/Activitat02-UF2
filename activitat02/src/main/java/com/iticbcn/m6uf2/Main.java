package com.iticbcn.m6uf2;

import java.io.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws IOException, SQLException, Exception{
        mostrarMenu();
    }
    private static void mostrarMenu() throws IOException, SQLException, Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("Hola, benvingut, selecciona una opció");
            System.out.println("1. Crea la base de dades");
            System.out.println("2. Crea les taules");
            System.out.println("3. Insereix en la taula Horari");
            System.out.println("4. Mostra els registres de 10 en 10");
            System.out.println("5. Genera XML");
            System.out.println("6. Mostra registre per ID");
            System.out.println("7. Mostra registres per un camp amb LIKE");
            System.out.println("8. Modifica camp");
            System.out.println("9. Esborrar registre per ID");
            System.out.println("10. Sortir");
            String resposta = reader.readLine();

            switch(resposta) {
                case "1" -> {
                    System.out.println("Perfecte, diu-me el nom de la base de dades");
                    String dbName = reader.readLine();
                    AccessDB.createDatabase(dbName);
                }
                case "2" -> {
                    System.out.println("Perfecte, en quina base de dades?");
                    String dbName = reader.readLine();
                    AccessDB.createTables(dbName);
                }
                case "3" -> {
                    System.out.println("Vale, diu-me quins horaris vols afegir? \n Primer la ID: ");
                    int id = Integer.parseInt(reader.readLine());
                    System.out.println("Perfecte, ara la hora de sortida");
                    String horaSortida = reader.readLine();
                    System.out.println("Ara l'hora d'arribada");
                    String horaArribada = reader.readLine();
                    System.out.println("Ara la data");
                    String data = reader.readLine();
                    System.out.println("Guai, faré l'inserció!");
                    AccessDB.insertHoraris(id, horaSortida, horaArribada, data);
                }
                case "4" -> {
                    System.out.println("Indica el número de pàgina que vols veure:");
                    int page = Integer.parseInt(reader.readLine());
                    AccessDB.mostraPagina(page);
                }
                case "5" -> {
                    System.out.println("Indica el nom de la base de dades");
                    String dbName = reader.readLine();
                    System.out.println("Generant XML...");
                    AccessDB.writeXML(dbName);
                }
                case "6" -> {
                    System.out.println("Indica el nom de la base de dades");
                    String dbName = reader.readLine();
                    System.out.println("Indica el nom de la taula");
                    String tableName = reader.readLine();
                    System.out.println("Indica l'id del registre que vols veure");
                    int id = Integer.parseInt(reader.readLine());
                    AccessDB.mostraRegistrePerId(dbName, tableName, id);
                }
                case "7" -> {
                    System.out.println("Indica el nom de la base de dades");
                    String dbName = reader.readLine();
                    System.out.println("Indica el nom de la taula");
                    String tableName = reader.readLine();
                    System.out.println("Indica el nom de la columna");
                    String columnName = reader.readLine();
                    System.out.println("Indica el patró de cerca");
                    String patro = reader.readLine();
                    AccessDB.mostraRegistrePerCamp(dbName, tableName, columnName, patro);
                }
                case "8" -> {
                    System.out.println("Indica el nom de la base de dades");
                    String dbName = reader.readLine();
                    System.out.println("Indica el nom de la taula");
                    String tableName = reader.readLine();
                    System.out.println("Indica el nom de la columna");
                    String columnName = reader.readLine();
                    System.out.println("Indica l'id del registre que vols modificar");
                    int id = Integer.parseInt(reader.readLine());
                    System.out.println("Introdueix el nou valor");
                    String nouValor = reader.readLine();
                    AccessDB.modificaCamp(dbName, tableName, id, columnName, nouValor);
                } 
                case "9" -> {
                    System.out.println("Indica el nom de la base de dades");
                    String dbName = reader.readLine();
                    System.out.println("Indica el nom de la taula");
                    String tableName = reader.readLine();
                    System.out.println("Indica l'id del registre que vols eliminar");
                    int id = Integer.parseInt(reader.readLine());
                    AccessDB.esborrarPerId(dbName, tableName, id);
                }
                case "10" -> {
                    System.out.println("Adéu");
                    return;
                }
                default -> System.out.println("Opció no vàlida. Tria 1, 2, 3, 4, 5, 6, 7, 8, 9 o 10");
            }
        }
    }
}