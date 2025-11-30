package org.example;

import java.sql.*;
import java.util.Scanner;

public class TrainerService {

    // Input validation helper
    private static Integer readIntLoop(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    // Function 1: Schedule View
    public static void viewSchedule(Scanner scanner) {
        System.out.println("\n--- Trainer Schedule ---");

        int trainerId = readIntLoop(scanner, "Enter trainer ID: ");

        String sql = "SELECT PT.session_id, M.name AS member_name, " +
                "PT.start_time, PT.end_time, PT.notes " +
                "FROM PTSession PT " +
                "JOIN Member M ON PT.member_id = M.member_id " +
                "WHERE PT.trainer_id = ? " +
                "AND PT.start_time > NOW() " +
                "ORDER BY PT.start_time ASC;";


        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, trainerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nUpcoming Sessions:");

            boolean hasSessions = false;

            while (rs.next()) {
                hasSessions = true;
                System.out.println("\nSession ID: " + rs.getInt("session_id"));
                System.out.println("Member: " + rs.getString("member_name"));
                System.out.println("Start: " + rs.getTimestamp("start_time"));
                System.out.println("End: " + rs.getTimestamp("end_time"));
                System.out.println("Notes: " + rs.getString("notes"));
                System.out.println("-----------------------------------");
            }

            if (!hasSessions) {
                System.out.println("No sessions found for this trainer.");
            }

        } catch (Exception e) {
            System.out.println("Error loading schedule:");
            e.printStackTrace();
        }
    }

    // Function 2: Member Lookup (partial match member name)
    public static void lookupMember(Scanner scanner) {
        System.out.println("\n--- Lookup Member ---");

        System.out.print("Enter member name (or part of name): ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("Search keyword cannot be empty.");
            return;
        }

        String sql = "SELECT member_id, name, email, phone, target_weight " +
                "FROM Member " +
                "WHERE LOWER(name) LIKE LOWER(?) " + // Partial match SQL
                "ORDER BY name ASC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            ResultSet rs = stmt.executeQuery();

            System.out.println("\nSearch Results:");

            boolean found = false;

            while (rs.next()) {
                found = true;

                int memberId = rs.getInt("member_id");

                System.out.println("\n-----------------------------------");
                System.out.println("Member ID: " + memberId);
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("Target Weight: " + rs.getDouble("target_weight"));

                // Load the latest health metric for this member
                String metricSql = "SELECT weight, heart_rate, latest_recorded_at AS recorded_at " +
                        "FROM MemberLatestMetric " +
                        "WHERE member_id = ?";

                try (PreparedStatement metricStmt = conn.prepareStatement(metricSql)) {
                    metricStmt.setInt(1, memberId);
                    ResultSet metricRs = metricStmt.executeQuery();

                    if (metricRs.next()) {
                        System.out.println("Latest Weight: " + metricRs.getDouble("weight"));
                        System.out.println("Latest Heart Rate: " + metricRs.getInt("heart_rate"));
                        System.out.println("Recorded At: " + metricRs.getTimestamp("recorded_at"));
                    } else {
                        System.out.println("No health metrics recorded yet.");
                    }
                }
            }
            System.out.println("-----------------------------------");


            if (!found) {
                System.out.println("No matching members found.");
            }

        } catch (Exception e) {
            System.out.println("Error searching member:");
            e.printStackTrace();
        }
    }
}
