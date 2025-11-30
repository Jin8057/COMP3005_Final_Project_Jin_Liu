package org.example;

import java.sql.*;
import java.util.Scanner;

public class MemberService {
    // Input validation helpers
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

    private static Double readDoubleLoop(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static Date readDateLoop(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Date.valueOf(input);  // requires YYYY-MM-DD
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date format. Use YYYY-MM-DD.");
            }
        }
    }

    // Function 1: User Registration
    public static void registerMember(Scanner scanner) {
        System.out.println("\n--- Register New Member ---");

        System.out.print("Enter name: ");
        String name = scanner.nextLine().trim();
        while (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            System.out.print("Enter name: ");
            name = scanner.nextLine().trim();
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        while (email.isEmpty()) {
            System.out.println("Email cannot be empty.");
            System.out.print("Enter email: ");
            email = scanner.nextLine().trim();
        }

        System.out.print("Enter phone: ");
        String phone = scanner.nextLine().trim();

        Date dob = readDateLoop(scanner, "Enter date of birth (YYYY-MM-DD): ");

        System.out.print("Enter gender: ");
        String gender = scanner.nextLine().trim();

        Double targetWeight = readDoubleLoop(scanner, "Enter target weight (kg): ");

        String sql = "INSERT INTO Member (name, email, phone, dob, gender, target_weight) " + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setDate(4, dob);
            stmt.setString(5, gender);
            stmt.setDouble(6, targetWeight);

            stmt.executeUpdate();
            System.out.println("Member registered successfully!");

        } catch (Exception e) {
            System.out.println("Error registering member:");
            e.printStackTrace();
        }
    }

    // Helper for field updates
    private static void updateField(String sql, Object value, int memberId) {
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, value);
            stmt.setInt(2, memberId);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                System.out.println("No member found with that ID.");
            }

        } catch (Exception e) {
            System.out.println("Error updating field:");
            e.printStackTrace();
        }
    }

    // Function 2: Profile Management
    public static void updateProfile(Scanner scanner) {
        System.out.println("\n--- Update Member Profile ---");

        Integer memberId = readIntLoop(scanner, "Enter member ID: ");

        while (true) {
            System.out.println("\nWhat would you like to update?");
            System.out.println("1. Phone");
            System.out.println("2. Target Weight");
            System.out.println("3. Email");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {

                case "1": {
                    System.out.print("Enter new phone: ");
                    String phone = scanner.nextLine().trim();
                    updateField("UPDATE Member SET phone = ? WHERE member_id = ?", phone, memberId);
                    System.out.println("Phone updated!");
                    break;
                }

                case "2": {
                    Double weight = readDoubleLoop(scanner, "Enter new target weight: ");
                    updateField("UPDATE Member SET target_weight = ? WHERE member_id = ?", weight, memberId);
                    System.out.println("Target weight updated!");
                    break;
                }

                case "3": {
                    System.out.print("Enter new email: ");
                    String email = scanner.nextLine().trim();
                    while (email.isEmpty()) {
                        System.out.println("Email cannot be empty.");
                        System.out.print("Enter new email: ");
                        email = scanner.nextLine().trim();
                    }
                    updateField("UPDATE Member SET email = ? WHERE member_id = ?", email, memberId);
                    System.out.println("Email updated!");
                    break;
                }

                case "0":
                    return;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // Function 3: Health History
    public static void addHealthMetric(Scanner scanner) {
        System.out.println("\n--- Add Health Metric ---");

        int memberId = readIntLoop(scanner, "Enter member ID: ");
        double weight = readDoubleLoop(scanner, "Enter weight (kg): ");
        int heartRate = readIntLoop(scanner, "Enter heart rate: ");

        String sql = "INSERT INTO HealthMetric (member_id, weight, heart_rate) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setDouble(2, weight);
            stmt.setInt(3, heartRate);

            stmt.executeUpdate();
            System.out.println("Health metric added!");

        } catch (Exception e) {
            System.out.println("Error adding health metric:");
            e.printStackTrace();
        }
    }

    // Function 4: Dashboard
    public static void viewDashboard(Scanner scanner) {
        System.out.println("\n--- Member Dashboard ---");

        int memberId = readIntLoop(scanner, "Enter member ID: ");

        String memberSql = "SELECT name, target_weight FROM Member WHERE member_id = ?";

        String metricSql = "SELECT weight, heart_rate, latest_recorded_at AS recorded_at " +
                "FROM MemberLatestMetric " +
                "WHERE member_id = ?";

        String sessionSql = "SELECT session_id, start_time, end_time, notes " +
                        "FROM PTSession " +
                        "WHERE member_id = ? AND start_time > NOW() " +
                        "ORDER BY start_time ASC";

        try (Connection conn = DBConnection.connect()) {

            // Load basic member profile
            try (PreparedStatement stmt = conn.prepareStatement(memberSql)) {
                stmt.setInt(1, memberId);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Member not found.");
                    return;
                }

                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Target Weight: " + rs.getDouble("target_weight"));
            }

            // Load latest health metric
            try (PreparedStatement stmt = conn.prepareStatement(metricSql)) {
                stmt.setInt(1, memberId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    System.out.println("\nLatest Health Metric:");
                    System.out.println("Weight: " + rs.getDouble("weight"));
                    System.out.println("Heart Rate: " + rs.getInt("heart_rate"));
                    System.out.println("Recorded At: " + rs.getTimestamp("recorded_at"));
                } else {
                    System.out.println("\nNo health metrics recorded yet.");
                }
            }

            // Load upcoming PT sessions
            try (PreparedStatement stmt = conn.prepareStatement(sessionSql)) {
                stmt.setInt(1, memberId);
                ResultSet rs = stmt.executeQuery();

                System.out.println("\nUpcoming PT Sessions:");

                boolean hasSessions = false;

                while (rs.next()) {
                    hasSessions = true;

                    Timestamp start = rs.getTimestamp("start_time");
                    Timestamp end = rs.getTimestamp("end_time");
                    System.out.println("\n-----------------------------------");
                    System.out.println("Session ID: " + rs.getInt("session_id"));
                    System.out.println("Start: " + start);
                    System.out.println("End: " + end);
                    System.out.println("Notes: " + rs.getString("notes"));
                    System.out.println("-----------------------------------");
                }

                if (!hasSessions) {
                    System.out.println("No upcoming sessions.");
                }
            }

        } catch (Exception e) {
            System.out.println("Error loading dashboard:");
            e.printStackTrace();
        }
    }
}
