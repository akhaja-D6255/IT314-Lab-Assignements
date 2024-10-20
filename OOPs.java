import java.io.*;
import java.util.*;

class User {
    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username + "," + password;
    }

    public static User fromString(String data) {
        String[] parts = data.split(",");
        return new User(parts[0], parts[1]);
    }
}

class Task {
    private String title;
    private String description;
    private boolean completed;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void complete() {
        this.completed = true;
    }

    @Override
    public String toString() {
        return title + "," + description + "," + completed;
    }

    public static Task fromString(String data) {
        String[] parts = data.split(",");
        Task task = new Task(parts[0], parts[1]);
        if (parts.length > 2) {
            task.completed = Boolean.parseBoolean(parts[2]);
        }
        return task;
    }
}

class UserManager {
    private List<User> users;
    private final String filename;

    public UserManager(String filename) {
        this.filename = filename;
        this.users = new ArrayList<>();
        loadUsers();
    }

    private void loadUsers() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                users.add(User.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public void saveUsers() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (User user : users) {
                bw.write(user.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public void registerUser(String username, String password) {
        users.add(new User(username, password));
        saveUsers();
        System.out.println("User registered successfully.");
    }

    public boolean authenticateUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }
}

class TaskManager {
    private List<Task> tasks;
    private final String filename;

    public TaskManager(String filename) {
        this.filename = filename;
        this.tasks = new ArrayList<>();
        loadTasks();
    }

    private void loadTasks() {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                tasks.add(Task.fromString(line));
            }
        } catch (IOException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
    }

    public void saveTasks() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Task task : tasks) {
                bw.write(task.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    public void addTask(String title, String description) {
        tasks.add(new Task(title, description));
        saveTasks();
        System.out.println("Task added successfully.");
    }

    public void listTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            String status = task.isCompleted() ? "✔️" : "❌";
            System.out.printf("%d. %s - %s (%s)\n", i + 1, task.getTitle(), status, task.getDescription());
        }
    }

    public void completeTask(int index) {
        if (index < 1 || index > tasks.size()) {
            System.out.println("Invalid task index.");
            return;
        }
        Task task = tasks.get(index - 1);
        task.complete();
        saveTasks();
        System.out.println("Task marked as completed.");
    }

    public void deleteTask(int index) {
        if (index < 1 || index > tasks.size()) {
            System.out.println("Invalid task index.");
            return;
        }
        tasks.remove(index - 1);
        saveTasks();
        System.out.println("Task deleted successfully.");
    }
}

public class TaskManagerApp {
    private static final String USER_FILE = "users.txt";
    private static final String TASK_FILE = "tasks.txt";
    private static UserManager userManager;
    private static TaskManager taskManager;
    private static String currentUser;

    public static void main(String[] args) {
        userManager = new UserManager(USER_FILE);
        taskManager = new TaskManager(TASK_FILE);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nTask Manager Application");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    registerUser(scanner);
                    break;
                case "2":
                    if (loginUser(scanner)) {
                        userMenu(scanner);
                    }
                    break;
                case "3":
                    System.out.println("Exiting the application.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        userManager.registerUser(username, password);
    }

    private static boolean loginUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (userManager.authenticateUser(username, password)) {
            currentUser = username;
            System.out.println("Login successful. Welcome, " + currentUser + "!");
            return true;
        } else {
            System.out.println("Invalid username or password. Please try again.");
            return false;
        }
    }

    private static void userMenu(Scanner scanner) {
        while (true) {
            System.out.println("\nUser Menu");
            System.out.println("1. Add Task");
            System.out.println("2. List Tasks");
            System.out.println("3. Complete Task");
            System.out.println("4. Delete Task");
            System.out.println("5. Logout");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    addTask(scanner);
                    break;
                case "2":
                    taskManager.listTasks();
                    break;
                case "3":
                    completeTask(scanner);
                    break;
                case "4":
                    deleteTask(scanner);
                    break;
                case "5":
                    System.out.println("Logging out.");
                    currentUser = null;
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addTask(Scanner scanner) {
        System.out.print("Enter task title: ");
        String title = scanner.nextLine();
        System.out.print("Enter task description: ");
        String description = scanner.nextLine();
        taskManager.addTask(title, description);
    }

    private static void completeTask(Scanner scanner) {
        taskManager.listTasks();
        System.out.print("Enter task number to complete: ");
        int taskNumber = Integer.parseInt(scanner.nextLine());
        taskManager.completeTask(taskNumber);
    }

    private static void deleteTask(Scanner scanner) {
        taskManager.listTasks();
        System.out.print("Enter task number to delete: ");
        int taskNumber = Integer.parseInt(scanner.nextLine());
        taskManager.deleteTask(taskNumber);
    }
}
