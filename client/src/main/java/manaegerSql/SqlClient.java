package manaegerSql;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.opencsv.CSVWriter;

import main.Client;

public class SqlClient {

    private static final String URL = Client.properties.getProperty("DbUrl");
    private static final String USER = Client.properties.getProperty("DbUser");
    private static final String PASSWORD = Client.properties.getProperty("DbPassword");
    private Connection con;

    public SqlClient() {
        try {
            System.out.println("Iniciando cliente sql...");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Cliente sql iniciado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeQuery(String pathFile) {
        String query;
        try {
            query = new String(Files.readAllBytes(Paths.get(pathFile)));
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo SQL", e);
        }

        try {
            Statement stmt = con.createStatement();
            System.out.println("Ejecutando consulta...");
            ResultSet rs = stmt.executeQuery(query);
            CSVWriter writer = new CSVWriter(new FileWriter("files/result.csv"));

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Escribir los nombres de las columnas en el CSV
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }
            writer.writeNext(columnNames);

            // Escribir las filas en el CSV
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = rs.getString(i + 1);
                }
                writer.writeNext(row);
            }
            System.out.println("Consulta ejecutada, guardando resultados");
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error al ejecutar la consulta o escribir el CSV", e);
        }
    }

    public void disconnect() {
        try {
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
