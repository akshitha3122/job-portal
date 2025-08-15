package com.learnJDBC;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class JobPost {
    private final Connection connection;
    private final Scanner scanner;

    public JobPost(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void postJob(Company company) {
        try {
            System.out.print("Enter Company ID: ");
            int companyId = Integer.parseInt(scanner.nextLine().trim());

            if (!company.getCompanyByID(companyId)) {
                System.out.println("Company not found.");
                return;
            }

            System.out.print("Enter Job Title: ");
            String title = scanner.nextLine().trim();
            System.out.print("Enter Job Description: ");
            String description = scanner.nextLine().trim();
            System.out.print("Enter Salary (e.g., 50000): ");
            String salaryStr = scanner.nextLine().trim();
            double salary = salaryStr.isEmpty() ? 0.0 : Double.parseDouble(salaryStr);

            String query = "INSERT INTO job_posts(company_id, title, description, salary, posted_date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, companyId);
                ps.setString(2, title);
                ps.setString(3, description);
                ps.setDouble(4, salary);
                ps.setDate(5, Date.valueOf(LocalDate.now()));
                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Job Posted Successfully" : "Failed to post job");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewJobPosts() {
        String query = "SELECT j.id, j.title, j.salary, j.posted_date, c.name AS company_name FROM job_posts j JOIN companies c ON j.company_id = c.id";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Job Posts:");
            System.out.println("+----+-------------------------+-----------------+------------+------------+");
            System.out.println("| ID | Title                   | Company         | Salary     | PostedDate |");
            System.out.println("+----+-------------------------+-----------------+------------+------------+");
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String companyName = rs.getString("company_name");
                double salary = rs.getDouble("salary");
                Date date = rs.getDate("posted_date");
                System.out.printf("|%-4d|%-25s|%-17s|%-12.2f|%-12s|\n", id, title, companyName, salary, date);
                System.out.println("+----+-------------------------+-----------------+------------+------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getJobByID(int id) {
        String query = "SELECT id FROM job_posts WHERE id = ?";
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
