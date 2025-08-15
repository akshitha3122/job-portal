package com.learnJDBC;
import java.sql.*;
import java.util.Scanner;

public class Candidate {
	private final Connection connection;
	private final Scanner scanner;
	public Candidate(Connection connection,Scanner scanner) {
		this.connection=connection;
		this.scanner=scanner;
	}
	public void registerCandidate() {
		try {
			System.out.print("Enter Candidate Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Skills (comma separated): ");
            String skills = scanner.nextLine().trim();
            String query="INSERT INTO candidates(name,email,skills) VALUES(?,?,?)";
            try(PreparedStatement ps=connection.prepareStatement(query)){
            	ps.setString(1, name);
            	ps.setString(2, email);
            	ps.setString(3, skills);
            	int rows = ps.executeUpdate();
                System.out.println(rows > 0 ? "Candidate Registered Successfully" : "Failed to register candidate");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate")) {
                System.out.println("A candidate with this email already exists.");
            } else {
                e.printStackTrace();
            }
        }
    }

    public void viewCandidates() {
        String query = "SELECT * FROM candidates";
        try (PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("Candidates:");
            System.out.println("+----+-------------------------+-------------------------+-------------------------+");
            System.out.println("| ID | Name                    | Email                   | Skills                  |");
            System.out.println("+----+-------------------------+-------------------------+-------------------------+");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String skills = rs.getString("skills");
                System.out.printf("|%-4d|%-25s|%-25s|%-25s|\n", id, name, email, skills);
                System.out.println("+----+-------------------------+-------------------------+-------------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getCandidateByID(int id) {
        String query = "SELECT id FROM candidates WHERE id = ?";
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


