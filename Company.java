package com.learnJDBC;

import java.sql.*;
import java.util.Scanner;

public class Company {
    private final Connection connection;
    private final Scanner scanner;

    public Company(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addCompany() {
        try {
            System.out.print("Enter Company Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Location: ");
            String location = scanner.nextLine().trim();
            System.out.print("Enter Industry: ");
            String industry = scanner.nextLine().trim();

            String query = "INSERT INTO companies(name, location, industry) VALUES (?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, name);
                ps.setString(2, location);
                ps.setString(3, industry);
                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Company Added Successfully" : "Failed to add company");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewCompanies() {
        String query = "SELECT * FROM companies";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Companies:");
            System.out.println("+----+-------------------------+-----------------+----------------+");
            System.out.println("| ID | Name                    | Location        | Industry       |");
            System.out.println("+----+-------------------------+-----------------+----------------+");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String location = rs.getString("location");
                String industry = rs.getString("industry");
                System.out.printf("|%-4d|%-25s|%-17s|%-16s|\n", id, name, location, industry);
                System.out.println("+----+-------------------------+-----------------+----------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getCompanyByID(int id) {
        String query = "SELECT id FROM companies WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
