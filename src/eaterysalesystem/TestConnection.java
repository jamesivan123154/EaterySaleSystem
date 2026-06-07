/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eaterysalesystem;

/**
 *
 * @author james
 */
import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/eaterydb",
        "postgres",
        "admin123")) {

    System.out.println("Connected!");

} catch (Exception e) {
    e.printStackTrace();
}
    }
}