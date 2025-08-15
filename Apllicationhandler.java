package com.learnJDBC;

import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class ApplicationHandler {
    private final Connection connection;
    private final Scanner scanner;

    public ApplicationHandler(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void applyForJob(Candidate candidate, JobPost jobPost) {
        try {
            System.out.print("Enter Candidate ID: ");
            int candidateId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter Job ID: ");
            int jobId = Integer.parseInt(scanner.nextLine().trim());

            if (!candidate.getCandidateByID(candidateId)) {
                System.out.println("Candidate not found.");
                return;
            }
            if (!jobPost.getJobByID(jobId)) {
                System.out.println("Job not found.");
                return;
            }

            String query = "INSERT INTO applications(candidate_id, job_id, application_date, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, candidateId);
                ps.setInt(2, jobId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.setString(4, "Pending");
                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Application submitted successfully" : "Failed to submit application");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewApplications() {
        String query = "SELECT a.id, c.name AS candidate_name, j.title AS job_title, a.application_date, a.status " +
                       "FROM applications a " +
                       "JOIN candidates c ON a.candidate_id = c.id " +
                       "JOIN job_posts j ON a.job_id = j.id";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Applications:");
            System.out.println("+----+-------------------------+-------------------------+------------+------------+");
            System.out.println("| ID | Candidate               | Job                     | AppDate    | Status     |");
            System.out.println("+----+-------------------------+-------------------------+------------+------------+");
            while (rs.next()) {
                int id = rs.getInt("id");
                String cand = rs.getString("candidate_name");
                String job = rs.getString("job_title");
                Date date = rs.getDate("application_date");
                String status = rs.getString("status");
                System.out.printf("|%-4d|%-25s|%-25s|%-12s|%-12s|\n", id, cand, job, date, status);
                System.out.println("+----+-------------------------+-------------------------+------------+------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateApplicationStatus() {
        try {
            System.out.print("Enter Application ID: ");
            int appId = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter new status (Pending/Shortlisted/Rejected/Hired): ");
            String status = scanner.nextLine().trim();

            String query = "UPDATE applications SET status = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, status);
                ps.setInt(2, appId);
                int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Status updated successfully" : "Failed to update status (check ID)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
