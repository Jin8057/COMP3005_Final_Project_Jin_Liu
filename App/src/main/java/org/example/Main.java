package org.example;

import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("=====================================");
        System.out.println("   Fitness Club Management System    ");
        System.out.println("=====================================");

        while (true) {
            System.out.println("\nMain Menu:");
            System.out.println("1. Member Functions");
            System.out.println("2. Trainer Functions");
            System.out.println("3. Admin Functions");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    memberMenu();
                    break;
                case "2":
                    trainerMenu();
                    break;
                case "3":
                    adminMenu();
                    break;
                case "0":
                    System.out.println("Exiting system... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    // Member Menu
    private static void memberMenu() {
        while (true) {
            System.out.println("\nMember Menu:");
            System.out.println("1. Register Member");
            System.out.println("2. Update Profile");
            System.out.println("3. Add Health Metric");
            System.out.println("4. View Dashboard");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    MemberService.registerMember(scanner);
                    break;
                case "2":
                    MemberService.updateProfile(scanner);
                    break;
                case "3":
                    MemberService.addHealthMetric(scanner);
                    break;
                case "4":
                    MemberService.viewDashboard(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Trainer Menu
    private static void trainerMenu() {
        while (true) {
            System.out.println("\nTrainer Menu:");
            System.out.println("1. View My Schedule");
            System.out.println("2. Lookup Member");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    TrainerService.viewSchedule(scanner);
                    break;
                case "2":
                    TrainerService.lookupMember(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Admin Menu
    private static void adminMenu() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Equipment Maintenance");
            System.out.println("2. Create or Pay Bill");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    AdminService.manageMaintenance(scanner);
                    break;
                case "2":
                    AdminService.manageBilling(scanner);
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
