package org.example;

import java.sql.*;
import java.util.Scanner;

public class AdminService {
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

    // Function 1: Equipment Maintenance
    public static void manageMaintenance(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Equipment Maintenance ---");
            System.out.println("1. Create Maintenance Log");
            System.out.println("2. View All Maintenance Logs");
            System.out.println("3. Update Maintenance Status");
            System.out.println("4. View Equipment List");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createMaintenanceLog(scanner);
                    break;
                case "2":
                    viewMaintenanceLogs();
                    break;
                case "3":
                    updateMaintenanceStatus(scanner);
                    break;
                case "4":
                    viewEquipmentList();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Function 1.1: Create Maintenance Log
    public static void createMaintenanceLog(Scanner scanner) {
        System.out.println("\n--- Create Maintenance Log ---");

        int equipmentId = readIntLoop(scanner, "Enter equipment ID: ");

        // Load equipment name + room
        String eqSql = "SELECT name, room FROM Equipment WHERE equipment_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement eqStmt = conn.prepareStatement(eqSql)) {

            eqStmt.setInt(1, equipmentId);
            ResultSet eqRs = eqStmt.executeQuery();

            if (!eqRs.next()) {
                System.out.println("Equipment ID not found.");
                return;
            }

            String eqName = eqRs.getString("name");
            String eqRoom = eqRs.getString("room");

            System.out.println("\nAssociated Equipment:");
            System.out.println("Name: " + eqName);
            System.out.println("Room: " + eqRoom);
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("Error loading equipment:");
            e.printStackTrace();
            return;
        }

        System.out.print("Enter issue description: ");
        String description = scanner.nextLine().trim();

        while (description.isEmpty()) {
            System.out.println("Description cannot be empty.");
            System.out.print("Enter issue description: ");
            description = scanner.nextLine().trim();
        }

        String insertSql = "INSERT INTO MaintenanceLog (equipment_id, description, status) " +
                "VALUES (?, ?, 'open')";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setInt(1, equipmentId);
            stmt.setString(2, description);
            stmt.executeUpdate();

            System.out.println("\nMaintenance log created successfully!");
            System.out.println("Equipment ID: " + equipmentId);
            System.out.println("Description: " + description);
            System.out.println("Status: open");
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("Error creating maintenance log:");
            e.printStackTrace();
        }
    }

    // Function 1.2: View All Maintenance Logs
    public static void viewMaintenanceLogs() {
        System.out.println("\n--- Maintenance Logs ---");

        String sql = "SELECT ml.log_id, ml.description, ml.status, ml.reported_at, ml.resolved_at, " +
                "eq.name AS equipment_name, eq.room AS equipment_room " +
                "FROM MaintenanceLog ml " +
                "JOIN Equipment eq ON ml.equipment_id = eq.equipment_id " +
                "ORDER BY ml.log_id ASC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nLog ID: " + rs.getInt("log_id"));
                System.out.println("Equipment: " + rs.getString("equipment_name"));
                System.out.println("Room: " + rs.getString("equipment_room"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Reported At: " + rs.getTimestamp("reported_at"));
                System.out.println("Resolved At: " + rs.getTimestamp("resolved_at"));
                System.out.println("-----------------------------------");
            }

            if (!found) {
                System.out.println("No maintenance logs found.");
            }

        } catch (Exception e) {
            System.out.println("Error loading logs:");
            e.printStackTrace();
        }
    }

    // Function 1.3: Update Maintenance Status
    public static void updateMaintenanceStatus(Scanner scanner) {
        System.out.println("\n--- Update Maintenance Status ---");

        int logId = readIntLoop(scanner, "Enter log ID: ");

        // Load the log
        String sql = "SELECT status FROM MaintenanceLog WHERE log_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, logId);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Log ID not found.");
                return;
            }

            String currentStatus = rs.getString("status");
            System.out.println("Current Status: " + currentStatus);

        } catch (Exception e) {
            System.out.println("Error loading log:");
            e.printStackTrace();
            return;
        }

        System.out.println("\nChoose new status:");
        System.out.println("1. open");
        System.out.println("2. resolved");
        System.out.print("Option: ");

        String choice = scanner.nextLine().trim();
        String newStatus = null;

        switch (choice) {
            case "1": newStatus = "open"; break;
            case "2": newStatus = "resolved"; break;
            default:
                System.out.println("Invalid option.");
                return;
        }

        // Update status
        String updateSql = "UPDATE MaintenanceLog SET status = ? WHERE log_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt2 = conn.prepareStatement(updateSql)) {

            stmt2.setString(1, newStatus);
            stmt2.setInt(2, logId);
            stmt2.executeUpdate();

            System.out.println("\nStatus updated successfully!");
            System.out.println("Log ID: " + logId);
            System.out.println("New Status: " + newStatus);
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("Error updating status:");
            e.printStackTrace();
        }
    }

    // Function 1.4: View Equipment List
    public static void viewEquipmentList() {
        System.out.println("\n--- Equipment List ---");

        String sql = "SELECT equipment_id, name, room FROM Equipment ORDER BY equipment_id ASC";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("\nEquipment ID: " + rs.getInt("equipment_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Room: " + rs.getString("room"));
                System.out.println("-----------------------------------");
            }

            if (!found) {
                System.out.println("No equipment found.");
            }

        } catch (Exception e) {
            System.out.println("Error loading equipment:");
            e.printStackTrace();
        }
    }

    // Function 2: Billing & Payment
    public static void manageBilling(Scanner scanner) {
        System.out.println("\n--- Billing Management ---");

        System.out.println("1. Create New Bill");
        System.out.println("2. Pay Existing Bill");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {

            case "1":
                createBill(scanner);
                break;

            case "2":
                payBill(scanner);
                break;

            case "0":
                return;

            default:
                System.out.println("Invalid option.");
        }
    }

    // Function 2.1: Create Bill
    private static void createBill(Scanner scanner) {
        System.out.println("\n--- Create New Bill ---");

        int memberId = readIntLoop(scanner, "Enter member ID: ");
        double amount = readDoubleLoop(scanner, "Enter amount: ");

        String sql = "INSERT INTO Billing (member_id, amount, status) " +
                "VALUES (?, ?, 'unpaid')";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, memberId);
            stmt.setDouble(2, amount);

            stmt.executeUpdate();

            System.out.println("\n-----------------------------------");
            System.out.println("\nBill created successfully!");
            System.out.println("Member ID: " + memberId);
            System.out.println("Amount: $" + amount);
            System.out.println("Status: unpaid");
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("Error creating bill:");
            e.printStackTrace();
        }
    }

    // Function 2.2: Pay Bill
    private static void payBill(Scanner scanner) {
        System.out.println("\n--- Pay Bill ---");

        int billId = readIntLoop(scanner, "Enter bill ID: ");

        // Check if bill exists
        String checkSql = "SELECT amount, status, member_id FROM Billing WHERE bill_id = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, billId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Bill not found.");
                return;
            }

            double amount = rs.getDouble("amount");
            String status = rs.getString("status");
            int memberId = rs.getInt("member_id");
            System.out.println("\n-----------------------------------");
            System.out.println("\nBill Details:");
            System.out.println("Bill ID: " + billId);
            System.out.println("Member ID: " + memberId);
            System.out.println("Amount: $" + amount);
            System.out.println("Current Status: " + status);

            if (status.equals("paid")) {
                System.out.println("This bill is already paid.");
                return;
            }

            System.out.print("\nConfirm payment? (y/n): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("y")) {
                System.out.println("Payment cancelled.");
                return;
            }

            // Update the bill to "paid"
            String updateSql = "UPDATE Billing SET status = 'paid' WHERE bill_id = ?";

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setInt(1, billId);
                updateStmt.executeUpdate();
            }

            System.out.println("\nPayment successful!");
            System.out.println("Bill ID: " + billId);
            System.out.println("Status: paid");
            System.out.println("-----------------------------------");

        } catch (Exception e) {
            System.out.println("Error processing payment:");
            e.printStackTrace();
        }
    }
}