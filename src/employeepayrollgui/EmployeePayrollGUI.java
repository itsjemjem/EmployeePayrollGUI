/*
 * Enhanced Employee Payroll Management System with Login Authentication
 * Now includes user authentication using users.csv file
 */
package employeepayrollgui;

import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Login Window Class - First window to appear
class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    
    public LoginWindow() {
        initializeGUI();
        createUsersFileIfNotExists();
    }
    
    private void initializeGUI() {
        setTitle("Employee Payroll Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Title label
        JLabel titleLabel = new JLabel("Employee Payroll Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please login to continue");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 20, 10);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username label and field
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 5);
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 5, 10, 10);
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(10, 10, 10, 5);
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 5, 10, 10);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passwordField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setFont(new Font("Arial", Font.PLAIN, 14));
        loginButton.addActionListener(e -> performLogin());
        
        exitButton = new JButton("Exit");
        exitButton.setPreferredSize(new Dimension(100, 35));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Set Enter key functionality
        getRootPane().setDefaultButton(loginButton);
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocus());
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set focus on username field
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }
    
    private void createUsersFileIfNotExists() {
        File usersFile = new File("users.csv");
        if (!usersFile.exists()) {
            try (PrintWriter pw = new PrintWriter(new FileWriter("users.csv"))) {
                pw.println("username,password,fullName,role");
                pw.println("admin,admin123,System Administrator,Administrator");
                pw.println("hr,hr123,HR Manager,HR Manager");
                pw.println("payroll,payroll123,Payroll Officer,Payroll Officer");
                pw.println("manager,manager123,Department Manager,Manager");
                System.out.println("Created default users.csv file with sample accounts:");
                System.out.println("admin/admin123, hr/hr123, payroll/payroll123, manager/manager123");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error creating users.csv file: " + e.getMessage(),
                    "File Creation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.",
                "Login Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (authenticateUser(username, password)) {
            // Login successful - proceed to welcome window
            new WelcomeWindow().setVisible(true);
            dispose();
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this, 
                "Invalid username and/or password.\nPlease check your credentials and try again.",
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            
            // Clear password field and set focus back to username
            passwordField.setText("");
            usernameField.selectAll();
            usernameField.requestFocus();
        }
    }
    
    private boolean authenticateUser(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader("users.csv"))) {
            String line;
            boolean firstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                String[] values = line.split(",");
                if (values.length >= 2) {
                    String fileUsername = values[0].trim();
                    String filePassword = values[1].trim();
                    
                    if (fileUsername.equals(username) && filePassword.equals(password)) {
                        return true; // Authentication successful
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, 
                "Error reading users.csv file: " + e.getMessage() + 
                "\nPlease ensure the file exists and is accessible.",
                "Authentication Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        return false; // Authentication failed
    }
}

// Welcome Window Class - Second window after successful login
class WelcomeWindow extends JFrame {
    public WelcomeWindow() {
        initializeGUI();
    }
    
    private void initializeGUI() {
        setTitle("Welcome");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panel with some padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 40, 20, 40);
        gbc.gridx = 0; // Single column layout
        
        // Title label
        JLabel titleLabel = new JLabel("Employee Payroll Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 0;
        mainPanel.add(titleLabel, gbc);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Please choose an option:");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 40, 30, 40);
        mainPanel.add(subtitleLabel, gbc);
        
        // Search for specific employee button
        JButton searchEmployeeBtn = new JButton("Search for Specific Employee");
        searchEmployeeBtn.setPreferredSize(new Dimension(280, 40));
        searchEmployeeBtn.addActionListener(e -> openEmployeeSearchWindow());
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 40, 10, 40);
        mainPanel.add(searchEmployeeBtn, gbc);
        
        // View all employees button
        JButton viewAllBtn = new JButton("View All Employees");
        viewAllBtn.setPreferredSize(new Dimension(280, 40));
        viewAllBtn.addActionListener(e -> openEmployeeListWindow());
        gbc.gridy = 3;
        gbc.insets = new Insets(10, 40, 10, 40);
        mainPanel.add(viewAllBtn, gbc);
        
        // Logout button
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(120, 30));
        logoutBtn.addActionListener(e -> logout());
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 40, 10, 40);
        mainPanel.add(logoutBtn, gbc);
        
        // Exit button
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(120, 30));
        exitBtn.addActionListener(e -> System.exit(0));
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 40, 20, 40);
        mainPanel.add(exitBtn, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void openEmployeeSearchWindow() {
        new EmployeeSearchWindow();
        dispose();
    }
    
    private void openEmployeeListWindow() {
        new EmployeePayrollGUI().setVisible(true);
        dispose();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginWindow().setVisible(true);
            dispose();
        }
    }
}

// Employee Search Window Class
class EmployeeSearchWindow extends JFrame {
    private JTextField searchField;
    
    public EmployeeSearchWindow() {
        initializeGUI();
        setVisible(true);
    }
    
    private void initializeGUI() {
        setTitle("Search Employee");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        
        // Title
        JLabel titleLabel = new JLabel("Search for Employee");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Search field label
        JLabel searchLabel = new JLabel("Enter Employee ID:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 20, 10, 10);
        mainPanel.add(searchLabel, gbc);
        
        // Search field
        searchField = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.insets = new Insets(20, 10, 10, 20);
        mainPanel.add(searchField, gbc);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> searchEmployee());
        buttonPanel.add(searchBtn);
        
        JButton backBtn = new JButton("Back");
        backBtn.addActionListener(e -> goBack());
        buttonPanel.add(backBtn);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        buttonPanel.add(logoutBtn);
        
        JButton exitBtn = new JButton("Exit");
        exitBtn.addActionListener(e -> System.exit(0));
        buttonPanel.add(exitBtn);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 20, 20, 20);
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Set focus on search field and add Enter key listener
        searchField.requestFocusInWindow();
        searchField.addActionListener(e -> searchEmployee());
    }
    
    private void searchEmployee() {
        String employeeId = searchField.getText().trim();
        
        if (employeeId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Employee ID.");
            return;
        }
        
        // Load employees and search for the specific ID
        List<Employee> employees = loadEmployeesFromCSV();
        Employee foundEmployee = null;
        
        for (Employee emp : employees) {
            if (emp.empId.equals(employeeId)) {
                foundEmployee = emp;
                break;
            }
        }
        
        if (foundEmployee != null) {
            // Open employee details window
            new EmployeeDetailsFrame(foundEmployee, "attendance.csv");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Employee with ID '" + employeeId + "' not found.\nPlease check the ID and try again.",
                "Employee Not Found", 
                JOptionPane.WARNING_MESSAGE);
            searchField.selectAll();
        }
    }
    
    private void goBack() {
        new WelcomeWindow().setVisible(true);
        dispose();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginWindow().setVisible(true);
            dispose();
        }
    }
    
    private List<Employee> loadEmployeesFromCSV() {
        List<Employee> employees = new ArrayList<>();
        String csvFilePath = "employees.csv";
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values.length >= 13) {
                    Employee emp = new Employee(
                        values[0].trim(), // empId
                        values[1].trim(), // lastName
                        values[2].trim(), // firstName
                        values[3].trim(), // birthDate
                        Double.parseDouble(values[4].trim()), // hourlyRate
                        Double.parseDouble(values[5].trim()), // basicSalary
                        Double.parseDouble(values[6].trim()), // riceSubsidy
                        Double.parseDouble(values[7].trim()), // phoneAllowance
                        Double.parseDouble(values[8].trim()), // clothingAllowance
                        values[9].trim(), // sssNumber
                        values[10].trim(), // philHealthNumber
                        values[11].trim(), // tinNumber
                        values[12].trim()  // pagIbigNumber
                    );
                    employees.add(emp);
                }
            }
        } catch (IOException | NumberFormatException e) {
            Logger.getLogger(EmployeeSearchWindow.class.getName()).log(Level.WARNING, "Error loading employees from CSV: " + e.getMessage());
        }
        
        return employees;
    }
}

// Main Application Class
public class EmployeePayrollGUI extends JFrame {
    private static final Logger logger = Logger.getLogger(EmployeePayrollGUI.class.getName());
    
    // Constants
    public static final LocalTime EXPECTED_LOGIN = LocalTime.of(8, 0);
    public static final LocalTime EXPECTED_LOGOUT = LocalTime.of(17, 0);
    public static final int GRACE_PERIOD_MINUTES = 10;
    
    // GUI Components - Only displaying required fields
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField txtEmpId, txtLastName, txtFirstName, txtSSS, txtPhilHealth, txtTIN, txtPagIbig;
    private JButton btnViewEmployee, btnNewEmployee, btnUpdate, btnDelete, btnRefresh, btnBackToWelcome, btnLogout;
    
    // Data
    private List<Employee> employees = new ArrayList<>();
    private String csvFilePath = "employees.csv";
    private String attendanceCsvPath = "attendance.csv";
    
    public EmployeePayrollGUI() {
        initializeGUI();
        loadEmployeesFromCSV();
        updateTable();
    }
    
    private void initializeGUI() {
        setTitle("Employee Payroll Management System - Employee List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create table panel
        JPanel tablePanel = createTablePanel();
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        
        // Create button panel
        JPanel buttonPanel = createButtonPanel();
        
        // Add panels to main panel
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Set initial button states
        btnViewEmployee.setEnabled(false);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee List"));
        
        // Create table model - only showing required fields
        String[] columnNames = {"Employee Number", "Last Name", "First Name", "SSS Number", "PhilHealth Number", "TIN", "Pag-IBIG Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    displayEmployeeDetails(selectedRow);
                    btnViewEmployee.setEnabled(true);
                    btnUpdate.setEnabled(true);
                    btnDelete.setEnabled(true);
                } else {
                    clearForm();
                    btnViewEmployee.setEnabled(false);
                    btnUpdate.setEnabled(false);
                    btnDelete.setEnabled(false);
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
                
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        txtEmpId = new JTextField(10);
        txtEmpId.setEditable(false); // Make Employee ID non-editable
        txtEmpId.setBackground(Color.LIGHT_GRAY); // Grey out the field
        panel.add(txtEmpId, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        txtLastName = new JTextField(10);
        panel.add(txtLastName, gbc);
        
        gbc.gridx = 4;
        panel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 5;
        txtFirstName = new JTextField(10);
        panel.add(txtFirstName, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("SSS Number:"), gbc);
        gbc.gridx = 1;
        txtSSS = new JTextField(10);
        panel.add(txtSSS, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("PhilHealth:"), gbc);
        gbc.gridx = 3;
        txtPhilHealth = new JTextField(10);
        panel.add(txtPhilHealth, gbc);
        
        gbc.gridx = 4;
        panel.add(new JLabel("TIN:"), gbc);
        gbc.gridx = 5;
        txtTIN = new JTextField(10);
        panel.add(txtTIN, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Pag-IBIG:"), gbc);
        gbc.gridx = 1;
        txtPagIbig = new JTextField(10);
        panel.add(txtPagIbig, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        btnViewEmployee = new JButton("View Employee");
        btnNewEmployee = new JButton("New Employee");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnRefresh = new JButton("Refresh");
        btnBackToWelcome = new JButton("Back to Welcome");
        btnLogout = new JButton("Logout");
        
        btnViewEmployee.addActionListener(e -> viewEmployeeDetails());
        btnNewEmployee.addActionListener(e -> openNewEmployeeForm());
        btnUpdate.addActionListener(e -> updateEmployee());
        btnDelete.addActionListener(e -> deleteEmployee());
        btnRefresh.addActionListener(e -> refreshTable());
        btnBackToWelcome.addActionListener(e -> backToWelcome());
        btnLogout.addActionListener(e -> logout());
        
        panel.add(btnViewEmployee);
        panel.add(btnNewEmployee);
        panel.add(btnUpdate);
        panel.add(btnDelete);
        panel.add(btnRefresh);
        panel.add(btnBackToWelcome);
        panel.add(btnLogout);
        
        return panel;
    }
    
    private void backToWelcome() {
        new WelcomeWindow().setVisible(true);
        dispose();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginWindow().setVisible(true);
            dispose();
        }
    }
    
    // Method to generate the next employee ID (numbers only)
    private String generateNextEmployeeId() {
        int maxId = 0;
        
        // Read directly from CSV file to get the most up-to-date data
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values.length >= 1) {
                    String empId = values[0].trim();
                    try {
                        int currentId = Integer.parseInt(empId);
                        maxId = Math.max(maxId, currentId);
                    } catch (NumberFormatException e) {
                        // Skip invalid format - continue with next entry
                        logger.log(Level.WARNING, "Invalid employee ID format found: " + empId);
                    }
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error reading CSV file for ID generation: " + e.getMessage());
            // If file doesn't exist or can't be read, start from 0
        }
        
        // Also check the in-memory employees list in case CSV is not up to date
        for (Employee emp : employees) {
            try {
                int currentId = Integer.parseInt(emp.empId);
                maxId = Math.max(maxId, currentId);
            } catch (NumberFormatException e) {
                // Skip invalid format
                logger.log(Level.WARNING, "Invalid employee ID format in memory: " + emp.empId);
            }
        }
        
        return String.valueOf(maxId + 1);
    }
    
    private void displayEmployeeDetails(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < employees.size()) {
            Employee emp = employees.get(selectedRow);
            txtEmpId.setText(emp.empId);
            txtLastName.setText(emp.lastName);
            txtFirstName.setText(emp.firstName);
            txtSSS.setText(emp.sssNumber);
            txtPhilHealth.setText(emp.philHealthNumber);
            txtTIN.setText(emp.tinNumber);
            txtPagIbig.setText(emp.pagIbigNumber);
        }
    }
    
    private void clearForm() {
        txtEmpId.setText("");
        txtLastName.setText("");
        txtFirstName.setText("");
        txtSSS.setText("");
        txtPhilHealth.setText("");
        txtTIN.setText("");
        txtPagIbig.setText("");
    }
    
    private void viewEmployeeDetails() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            Employee selectedEmployee = employees.get(selectedRow);
            new EmployeeDetailsFrame(selectedEmployee, attendanceCsvPath);
        }
    }
    
    private void openNewEmployeeForm() {
        new NewEmployeeDialog(this);
    }
    
    private void updateEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            Employee emp = employees.get(selectedRow);
            // Note: empId is not updated since it's auto-generated and non-editable
            emp.lastName = txtLastName.getText();
            emp.firstName = txtFirstName.getText();
            emp.empFullName = emp.lastName + ", " + emp.firstName;
            emp.sssNumber = txtSSS.getText();
            emp.philHealthNumber = txtPhilHealth.getText();
            emp.tinNumber = txtTIN.getText();
            emp.pagIbigNumber = txtPagIbig.getText();
            
            saveEmployeesToCSV();
            updateTable();
            JOptionPane.showMessageDialog(this, "Employee updated successfully!");
        }
    }
    
    private void deleteEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow != -1) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this employee?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                employees.remove(selectedRow);
                saveEmployeesToCSV();
                updateTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!");
            }
        }
    }
    
    private void refreshTable() {
        loadEmployeesFromCSV();
        updateTable();
        clearForm();
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        for (Employee emp : employees) {
            Object[] row = {
                emp.empId,
                emp.lastName,
                emp.firstName,
                emp.sssNumber,
                emp.philHealthNumber,
                emp.tinNumber,
                emp.pagIbigNumber
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadEmployeesFromCSV() {
        employees.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values.length >= 13) {
                    Employee emp = new Employee(
                        values[0].trim(), // empId
                        values[1].trim(), // lastName
                        values[2].trim(), // firstName
                        values[3].trim(), // birthDate
                        Double.parseDouble(values[4].trim()), // hourlyRate
                        Double.parseDouble(values[5].trim()), // basicSalary
                        Double.parseDouble(values[6].trim()), // riceSubsidy
                        Double.parseDouble(values[7].trim()), // phoneAllowance
                        Double.parseDouble(values[8].trim()), // clothingAllowance
                        values[9].trim(), // sssNumber
                        values[10].trim(), // philHealthNumber
                        values[11].trim(), // tinNumber
                        values[12].trim()  // pagIbigNumber
                    );
                    employees.add(emp);
                }
            }
        } catch (IOException | NumberFormatException e) {
            logger.log(Level.WARNING, "Error loading employees from CSV: " + e.getMessage());
        }
    }
    
    private void saveEmployeesToCSV() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFilePath))) {
            pw.println("empId,lastName,firstName,birthDate,hourlyRate,basicSalary,riceSubsidy,phoneAllowance,clothingAllowance,sssNumber,philHealthNumber,tinNumber,pagIbigNumber");
            for (Employee emp : employees) {
                pw.println(String.format("%s,%s,%s,%s,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s,%s,%s",
                    emp.empId, emp.lastName, emp.firstName, emp.birthDate,
                    emp.hourlyRate, emp.basicSalary, emp.riceSubsidy,
                    emp.phoneAllowance, emp.clothingAllowance,
                    emp.sssNumber, emp.philHealthNumber, emp.tinNumber, emp.pagIbigNumber));
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error saving employees to CSV: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Error saving data to file.");
        }
    }
        
    public void addNewEmployee(Employee emp) {
        employees.add(emp);
        saveEmployeesToCSV();
        updateTable();
    }
    
    // Method to get the next employee ID for new employee dialog
    public String getNextEmployeeId() {
        return generateNextEmployeeId();
    }
    
    // Method to get employees list for validation in NewEmployeeDialog
    public List<Employee> getEmployeesList() {
        return new ArrayList<>(employees); // Return a copy to prevent external modification
    }
    
    // Government Contribution Methods
    static double calculateSSSContribution(double grossMonthlyPay, double benefits) {
        double grossPay = grossMonthlyPay - benefits;
        if (grossPay < 3250) return 135.00;
        if (grossPay < 3750) return 157.50;
        if (grossPay < 4250) return 180.00;
        if (grossPay < 4750) return 202.50;
        if (grossPay < 5250) return 225.00;
        if (grossPay < 5750) return 247.50;
        if (grossPay < 6250) return 270.00;
        if (grossPay < 6750) return 292.50;
        if (grossPay < 7250) return 315.00;
        if (grossPay < 7750) return 337.50;
        if (grossPay < 8250) return 360.00;
        if (grossPay < 8750) return 382.50;
        if (grossPay < 9250) return 405.00;
        if (grossPay < 9750) return 427.50;
        if (grossPay < 10250) return 450.00;
        if (grossPay < 10750) return 472.50;
        if (grossPay < 11250) return 495.00;
        if (grossPay < 11750) return 517.50;
        if (grossPay < 12250) return 540.00;
        if (grossPay < 12750) return 562.50;
        if (grossPay < 13250) return 585.00;
        if (grossPay < 13750) return 607.50;
        if (grossPay < 14250) return 630.00;
        if (grossPay < 14750) return 652.50;
        if (grossPay < 15250) return 675.00;
        if (grossPay < 15750) return 697.50;
        if (grossPay < 16250) return 720.00;
        if (grossPay < 16750) return 742.50;
        if (grossPay < 17250) return 765.00;
        if (grossPay < 17750) return 787.50;
        if (grossPay < 18250) return 810.00;
        if (grossPay < 18750) return 832.50;
        if (grossPay < 19250) return 855.00;
        if (grossPay < 19750) return 877.50;
        if (grossPay < 20250) return 900.00;
        if (grossPay < 20750) return 922.50;
        if (grossPay < 21250) return 945.00;
        if (grossPay < 21750) return 967.50;
        if (grossPay < 22250) return 990.00;
        if (grossPay < 22750) return 1012.50;
        if (grossPay < 23250) return 1035.00;
        if (grossPay < 23750) return 1057.50;
        if (grossPay < 24250) return 1080.00;
        if (grossPay < 24750) return 1102.50;
        return 1125.00;
    }
    
    static double calculatePhilHealthContribution(double grossMonthlyPay, double benefits) {
        double grossPay = grossMonthlyPay - benefits;
        if (grossPay == 10000.00) return grossPay * 0.03 / 2;
        if (grossPay > 10000.00 && grossPay <= 59999.99) return grossPay * 0.03 / 2;
        return 60000 * 0.03 / 2;
    }
    
    static double calculatePagIbigContribution(double grossMonthlyPay, double benefits) {
        double grossPay = grossMonthlyPay - benefits;
        return (grossPay <= 1500) ? 50.00 : 100.00;
    }
    
    static double calculateTAXcontribution(double basicsalary, double sss, double philhealth, double pagibig) {
        double taxableIncome = basicsalary - (sss + philhealth + pagibig);
        if (taxableIncome <= 20832.00) {
            return 0;
        } else if (taxableIncome < 33333.00) {
            return (taxableIncome - 20833.00) * 0.20;
        } else if (taxableIncome < 66667.00) {
            return 2500.00 + (taxableIncome - 33333.00) * 0.25;
        } else if (taxableIncome < 166667.00) {
            return 10833.00 + (taxableIncome - 66667.00) * 0.30;
        } else if (taxableIncome < 666667.00) {
            return 40833.33 + (taxableIncome - 166667.00) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667.00) * 0.35;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Start with the login window instead of the welcome window
            new LoginWindow().setVisible(true);
        });
    }
}

// Employee Details Frame
class EmployeeDetailsFrame extends JFrame {
    private Employee employee;
    private String attendanceCsvPath;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JTextArea resultArea;
    
    public EmployeeDetailsFrame(Employee employee, String attendanceCsvPath) {
        this.employee = employee;
        this.attendanceCsvPath = attendanceCsvPath;
        initializeGUI();
        displayEmployeeInfo();
    }
    
    private void initializeGUI() {
        setTitle("Employee Details - " + employee.empFullName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Navigation button panel at the top
        JPanel navPanel = createNavigationPanel();
        
        // Employee info panel - only showing required fields
        JPanel infoPanel = createEmployeeInfoPanel();
        
        // Month selection panel
        JPanel monthPanel = createMonthSelectionPanel();
        
        // Result panel
        JPanel resultPanel = createResultPanel();
        
        add(navPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
        add(monthPanel, BorderLayout.EAST);
        add(resultPanel, BorderLayout.SOUTH);
        
        setSize(900, 650);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private JPanel createEmployeeInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Employee ID:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.empId), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 3;
        panel.add(new JLabel(employee.empFullName), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("SSS Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.sssNumber), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("PhilHealth Number:"), gbc);
        gbc.gridx = 3;
        panel.add(new JLabel(employee.philHealthNumber), gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("TIN Number:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(employee.tinNumber), gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Pag-IBIG Number:"), gbc);
        gbc.gridx = 3;
        panel.add(new JLabel(employee.pagIbigNumber), gbc);
        
        return panel;
    }
    
    private JPanel createMonthSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Salary Computation"));
        
        panel.add(new JLabel("Select Year:"));
        yearComboBox = new JComboBox<>();
        // Populate with years (current year and previous years)
        int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        for (int year = currentYear; year >= currentYear - 10; year--) {
            yearComboBox.addItem(String.valueOf(year));
        }
        panel.add(yearComboBox);
        
        panel.add(new JLabel("Select Month:"));
        monthComboBox = new JComboBox<>(new String[]{
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        });
        panel.add(monthComboBox);
        
        JButton computeButton = new JButton("Compute Salary");
        computeButton.addActionListener(e -> computeSalary());
        panel.add(computeButton);
        
        return panel;
    }
    
    private JPanel createResultPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Salary Details"));
        
        resultArea = new JTextArea(20, 60);
        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Navigation"));
        
        JButton searchBtn = new JButton("Search Employee");
        searchBtn.setPreferredSize(new Dimension(140, 30));
        searchBtn.addActionListener(e -> goToSearchWindow());
        panel.add(searchBtn);
        
        JButton viewAllBtn = new JButton("View Full List");
        viewAllBtn.setPreferredSize(new Dimension(120, 30));
        viewAllBtn.addActionListener(e -> goToFullList());
        panel.add(viewAllBtn);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.addActionListener(e -> logout());
        panel.add(logoutBtn);
        
        return panel;
    }
    
    private void goToSearchWindow() {
        new EmployeeSearchWindow();
        dispose();
    }
    
    private void goToFullList() {
        new EmployeePayrollGUI().setVisible(true);
        dispose();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginWindow().setVisible(true);
            dispose();
        }
    }
    
    private void displayEmployeeInfo() {
        StringBuilder info = new StringBuilder();
        info.append("Employee Details:\n");
        info.append("================\n");
        info.append("Employee ID: ").append(employee.empId).append("\n");
        info.append("Name: ").append(employee.empFullName).append("\n");
        info.append("SSS Number: ").append(employee.sssNumber).append("\n");
        info.append("PhilHealth Number: ").append(employee.philHealthNumber).append("\n");
        info.append("TIN Number: ").append(employee.tinNumber).append("\n");
        info.append("Pag-IBIG Number: ").append(employee.pagIbigNumber).append("\n");
        info.append("\nSelect a year and month, then click 'Compute Salary' to view payroll details.\n");
        
        resultArea.setText(info.toString());
    }
    
    private void computeSalary() {
        String selectedMonth = (String) monthComboBox.getSelectedItem();
        String selectedYear = (String) yearComboBox.getSelectedItem();
        int monthNumber = monthComboBox.getSelectedIndex() + 1;
        int year = Integer.parseInt(selectedYear);
        
        try {
            // Load attendance data for the employee for the selected year
            loadAttendanceData(year);
            
            // Calculate payroll for the selected year
            employee.calculatePayroll();
            
            // Display results
            displayPayrollResults(selectedMonth, monthNumber, year);
        } catch (NoAttendanceDataException e) {
            JOptionPane.showMessageDialog(this, 
                "No attendance data found for " + employee.empFullName + " in year " + year + ".\n" +
                "Please check if:\n" +
                "1. The employee was working during that year\n" +
                "2. The attendance file contains data for " + year + "\n" +
                "3. The employee ID is correct in the attendance records",
                "No Attendance Data Found", 
                JOptionPane.WARNING_MESSAGE);
            
            // Display a message in the result area
            StringBuilder result = new StringBuilder();
            result.append("========No Attendance Data Found========\n");
            result.append("Employee Number: ").append(employee.empId).append("\n");
            result.append("Name: ").append(employee.empFullName).append("\n");
            result.append("Selected Period: ").append(selectedMonth).append(" ").append(year).append("\n\n");
            result.append("No attendance records found for this employee in the selected year.\n");
            result.append("Please verify:\n");
            result.append("- Employee was working during ").append(year).append("\n");
            result.append("- Attendance file contains data for ").append(year).append("\n");
            result.append("- Employee ID matches records in attendance file\n");
            resultArea.setText(result.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error processing salary computation: " + e.getMessage(),
                "Computation Error", 
                JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(EmployeeDetailsFrame.class.getName()).log(Level.SEVERE, 
                "Error during salary computation", e);
        }
    }
    
    private void loadAttendanceData(int year) throws NoAttendanceDataException {
        employee.attendanceList.clear();
        boolean foundData = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(attendanceCsvPath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                String[] values = line.split(",");
                if (values.length >= 6 && values[0].trim().equals(employee.empId)) {
                    String dateStr = values[3].trim();
                    
                    // Check if the date belongs to the selected year
                    try {
                        // Handle date format like "6/3/24" or "6/3/2024"
                        int recordYear = parseYearFromDate(dateStr);
                        
                        if (recordYear == year) {
                            employee.addAttendance(values[3].trim(), values[4].trim(), values[5].trim());
                            foundData = true;
                        }
                    } catch (ParseException e) {
                        Logger.getLogger(EmployeeDetailsFrame.class.getName()).log(Level.WARNING, 
                            "Invalid date format in attendance record: " + dateStr);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading attendance data: " + e.getMessage());
            throw new NoAttendanceDataException("Failed to read attendance file: " + e.getMessage());
        }
        
        if (!foundData) {
            throw new NoAttendanceDataException("No attendance data found for employee " + 
                employee.empId + " in year " + year);
        }
    }
    
    private int parseYearFromDate(String dateStr) throws ParseException {
        // Handle different date formats: "6/3/24", "6/3/2024", "06/03/24", etc.
        String[] dateParts = dateStr.split("/");
        if (dateParts.length != 3) {
            throw new ParseException("Invalid date format: " + dateStr, 0);
        }
        
        String yearPart = dateParts[2].trim();
        int year;
        
        if (yearPart.length() == 2) {
            // Handle 2-digit year (e.g., "24" for 2024)
            int shortYear = Integer.parseInt(yearPart);
            // Assume years 00-30 are 2000-2030, and 31-99 are 1931-1999
            if (shortYear <= 30) {
                year = 2000 + shortYear;
            } else {
                year = 1900 + shortYear;
            }
        } else if (yearPart.length() == 4) {
            // Handle 4-digit year (e.g., "2024")
            year = Integer.parseInt(yearPart);
        } else {
            throw new ParseException("Invalid year format: " + yearPart, 0);
        }
        
        return year;
    }
    
    private void displayPayrollResults(String selectedMonth, int monthNumber, int year) {
        StringBuilder result = new StringBuilder();
        result.append("========Employee Payroll Summary========\n");
        result.append("Employee Number: ").append(employee.empId).append("\n");
        result.append("Name: ").append(employee.empFullName).append("\n");
        result.append("Birthday: ").append(employee.birthDate).append("\n");
        result.append("Period: ").append(selectedMonth).append(" ").append(year).append("\n");
        
        // Get weekly details for the selected month
        Map<Integer, WeeklyDetail> weekMap = employee.monthlyWeeklyDetails.get(monthNumber);
        
        if (weekMap == null || weekMap.isEmpty()) {
            result.append("\nNo attendance data found for ").append(selectedMonth).append(" ").append(year).append("\n");
            result.append("This could mean:\n");
            result.append("- No work days recorded for this month\n");
            result.append("- Employee was not working during this period\n");
            result.append("- Attendance data is incomplete for this month\n");
            resultArea.setText(result.toString());
            return;
        }
        
        double grossMonthlyPay = 0;
        int numWeeks = weekMap.size();
        double weeklyBenefit = (employee.riceSubsidy + employee.phoneAllowance + employee.clothingAllowance) / (double) numWeeks;
        
        result.append("\n------------------------------\n");
        result.append("Weekly Breakdown for Month: ").append(selectedMonth).append(" ").append(year).append("\n");
        
        // Display weekly details
        for (Map.Entry<Integer, WeeklyDetail> entry : weekMap.entrySet()) {
            int week = entry.getKey();
            WeeklyDetail wd = entry.getValue();
            
            double computedRegularPay = wd.regularHours * employee.hourlyRate;
            
            result.append("\nWeek ").append(week).append("\n");
            result.append("Regular Hours: ").append(wd.regularHours).append(" hrs\n");
            result.append("Regular Pay: Php ").append(String.format("%.2f", computedRegularPay)).append("\n");
            result.append("Days with Overtime: ").append(wd.overtimeHours).append(" day/s\n");
            if (wd.overtimeHours > 0) {
                result.append("Overtime Pay (25% bonus): Php ").append(String.format("%.2f", wd.totalEligibleOvertimePay)).append("\n");
            }
            result.append("Weekly Benefits: Php ").append(String.format("%.2f", weeklyBenefit)).append("\n");
            result.append("Weekly Salary (Net): Php ").append(String.format("%.2f", wd.weeklySalary)).append("\n");
            
            grossMonthlyPay += wd.weeklySalary;
        }
        
        // Calculate government deductions
        double benefits = employee.riceSubsidy + employee.phoneAllowance + employee.clothingAllowance;
        double sss = EmployeePayrollGUI.calculateSSSContribution(grossMonthlyPay, benefits);
        double philHealth = EmployeePayrollGUI.calculatePhilHealthContribution(grossMonthlyPay, benefits);
        double pagIbig = EmployeePayrollGUI.calculatePagIbigContribution(grossMonthlyPay, benefits);
        double tax = EmployeePayrollGUI.calculateTAXcontribution(employee.basicSalary, sss, pagIbig, philHealth);
        
        double netPayMonth = grossMonthlyPay - (sss + philHealth + pagIbig + tax);
        
        // Display monthly summary
        result.append("\nMonthly Summary (").append(selectedMonth).append(" ").append(year).append(")\n");
        result.append("Total Gross Pay: Php ").append(String.format("%.2f", grossMonthlyPay)).append("\n");
        result.append("\nBenefits (Monthly):\n");
        result.append("Rice Subsidy: Php ").append(String.format("%.2f", employee.riceSubsidy)).append("\n");
        result.append("Phone Allowance: Php ").append(String.format("%.2f", employee.phoneAllowance)).append("\n");
        result.append("Clothes Allowance: Php ").append(String.format("%.2f", employee.clothingAllowance)).append("\n");
        
        result.append("\nMandated Government Deductions:\n");
        result.append("SSS: Php ").append(String.format("%.2f", sss)).append("\n");
        result.append("PhilHealth: Php ").append(String.format("%.2f", philHealth)).append("\n");
        result.append("Pag-Ibig: Php ").append(String.format("%.2f", pagIbig)).append("\n");
        result.append("Tax: Php ").append(String.format("%.2f", tax)).append("\n");
        
        result.append("\nNet Pay Summary (").append(selectedMonth).append(" ").append(year).append(")\n");
        result.append("Gross Pay (Benefits Included): Php ").append(String.format("%.2f", grossMonthlyPay)).append("\n");
        result.append("Benefits: Php ").append(String.format("%.2f", benefits)).append("\n");
        result.append("Mandated Deductions: Php ").append(String.format("%.2f", (sss + philHealth + pagIbig + tax))).append("\n");
        result.append("Net Pay: Php ").append(String.format("%.2f", netPayMonth)).append("\n");
        
        resultArea.setText(result.toString());
    }
}

// Custom Exception for No Attendance Data
class NoAttendanceDataException extends Exception {
    public NoAttendanceDataException(String message) {
        super(message);
    }
}

// New Employee Dialog
class NewEmployeeDialog extends JDialog {
    private EmployeePayrollGUI parent;
    private JTextField txtEmpId, txtLastName, txtFirstName, txtSSS, txtPhilHealth, txtTIN, txtPagIbig;
    
    public NewEmployeeDialog(EmployeePayrollGUI parent) {
        super(parent, "New Employee", true);
        this.parent = parent;
        initializeGUI();
    }
    
    private void initializeGUI() {
        setLayout(new BorderLayout());
        
        JPanel formPanel = createFormPanel();
        JPanel buttonPanel = createButtonPanel();
        
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBorder(BorderFactory.createTitledBorder("Employee Information (* indicates required fields)"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1 - Employee ID (auto-generated and non-editable)
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel empIdLabel = new JLabel("Employee ID: *");
        empIdLabel.setForeground(Color.BLACK);
        panel.add(empIdLabel, gbc);
        gbc.gridx = 1;
        txtEmpId = new JTextField(15);
        txtEmpId.setEditable(false); // Make Employee ID non-editable
        txtEmpId.setBackground(Color.LIGHT_GRAY); // Grey out the field
        txtEmpId.setText(parent.getNextEmployeeId()); // Auto-generate the ID
        panel.add(txtEmpId, gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lastNameLabel = new JLabel("Last Name: *");
        lastNameLabel.setForeground(Color.BLACK);
        panel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        txtLastName = new JTextField(15);
        panel.add(txtLastName, gbc);
        
        // Row 3
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel firstNameLabel = new JLabel("First Name: *");
        firstNameLabel.setForeground(Color.BLACK);
        panel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(15);
        panel.add(txtFirstName, gbc);
        
        // Row 4
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel sssLabel = new JLabel("SSS Number: *");
        sssLabel.setForeground(Color.BLACK);
        panel.add(sssLabel, gbc);
        gbc.gridx = 1;
        txtSSS = new JTextField(15);
        txtSSS.setToolTipText("Format: 12-3456789-0 or 1234567890");
        panel.add(txtSSS, gbc);
        
        // Row 5
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel philHealthLabel = new JLabel("PhilHealth Number: *");
        philHealthLabel.setForeground(Color.BLACK);
        panel.add(philHealthLabel, gbc);
        gbc.gridx = 1;
        txtPhilHealth = new JTextField(15);
        txtPhilHealth.setToolTipText("Format: 12-345678901-2 or 123456789012");
        panel.add(txtPhilHealth, gbc);
        
        // Row 6
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel tinLabel = new JLabel("TIN Number: *");
        tinLabel.setForeground(Color.BLACK);
        panel.add(tinLabel, gbc);
        gbc.gridx = 1;
        txtTIN = new JTextField(15);
        txtTIN.setToolTipText("Format: 123-456-789-000 or 123456789000");
        panel.add(txtTIN, gbc);
        
        // Row 7
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel pagIbigLabel = new JLabel("Pag-IBIG Number: *");
        pagIbigLabel.setForeground(Color.BLACK);
        panel.add(pagIbigLabel, gbc);
        gbc.gridx = 1;
        txtPagIbig = new JTextField(15);
        txtPagIbig.setToolTipText("Format: 1234-5678-9012 or 123456789012");
        panel.add(txtPagIbig, gbc);
        
        // Add a note about required fields
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        JLabel noteLabel = new JLabel("<html><i>Note: All fields marked with * are required.<br>Government numbers will be validated for proper format and uniqueness.</i></html>");
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        noteLabel.setForeground(Color.BLACK);
        panel.add(noteLabel, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveEmployee());
        cancelButton.addActionListener(e -> dispose());
        
        panel.add(saveButton);
        panel.add(cancelButton);
        
        return panel;
    }
    
    private void saveEmployee() {
        String empId = txtEmpId.getText().trim(); // Use the auto-generated ID
        String lastName = txtLastName.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String sssNumber = txtSSS.getText().trim();
        String philHealthNumber = txtPhilHealth.getText().trim();
        String tinNumber = txtTIN.getText().trim();
        String pagIbigNumber = txtPagIbig.getText().trim();
        
        // Validate all required fields
        StringBuilder errorMessage = new StringBuilder();
        boolean hasErrors = false;
        
        if (empId.isEmpty()) {
            errorMessage.append("- Employee ID is required\n");
            hasErrors = true;
        }
        
        if (lastName.isEmpty()) {
            errorMessage.append("- Last Name is required\n");
            hasErrors = true;
        }
        
        if (firstName.isEmpty()) {
            errorMessage.append("- First Name is required\n");
            hasErrors = true;
        }
        
        if (sssNumber.isEmpty()) {
            errorMessage.append("- SSS Number is required\n");
            hasErrors = true;
        }
        
        if (philHealthNumber.isEmpty()) {
            errorMessage.append("- PhilHealth Number is required\n");
            hasErrors = true;
        }
        
        if (tinNumber.isEmpty()) {
            errorMessage.append("- TIN Number is required\n");
            hasErrors = true;
        }
        
        if (pagIbigNumber.isEmpty()) {
            errorMessage.append("- Pag-IBIG Number is required\n");
            hasErrors = true;
        }
        
        if (hasErrors) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all required fields:\n\n" + errorMessage.toString(),
                "Required Fields Missing",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Additional validation for government numbers (basic format checking)
        if (!isValidSSS(sssNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid SSS Number format.\nPlease enter a valid SSS number (e.g., 12-3456789-0).",
                "Invalid SSS Number",
                JOptionPane.WARNING_MESSAGE);
            txtSSS.requestFocus();
            txtSSS.selectAll();
            return;
        }
        
        if (!isValidPhilHealth(philHealthNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid PhilHealth Number format.\nPlease enter a valid PhilHealth number (e.g., 12-345678901-2).",
                "Invalid PhilHealth Number",
                JOptionPane.WARNING_MESSAGE);
            txtPhilHealth.requestFocus();
            txtPhilHealth.selectAll();
            return;
        }
        
        if (!isValidTIN(tinNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid TIN format.\nPlease enter a valid TIN (e.g., 123-456-789-000).",
                "Invalid TIN",
                JOptionPane.WARNING_MESSAGE);
            txtTIN.requestFocus();
            txtTIN.selectAll();
            return;
        }
        
        if (!isValidPagIbig(pagIbigNumber)) {
            JOptionPane.showMessageDialog(this, 
                "Invalid Pag-IBIG Number format.\nPlease enter a valid Pag-IBIG number (e.g., 1234-5678-9012).",
                "Invalid Pag-IBIG Number",
                JOptionPane.WARNING_MESSAGE);
            txtPagIbig.requestFocus();
            txtPagIbig.selectAll();
            return;
        }
        
        // Check for duplicate government numbers
        if (isDuplicateGovernmentNumber(sssNumber, philHealthNumber, tinNumber, pagIbigNumber)) {
            return; // Error message already shown in the method
        }
        
        // Create employee with default values for non-displayed fields
        // These values can be set later through direct database/CSV editing if needed
        Employee newEmployee = new Employee(empId, lastName, firstName, 
                "01/01/1990", // Default birth date
                0, // Default hourly rate
                0, // Default basic salary
                0, // Default rice subsidy
                0, // Default phone allowance
                0, // Default clothing allowance
                sssNumber, philHealthNumber, tinNumber, pagIbigNumber);
        
        parent.addNewEmployee(newEmployee);
        JOptionPane.showMessageDialog(this, 
            "Employee added successfully!\n\n" +
            "Employee ID: " + empId + "\n" +
            "Name: " + firstName + " " + lastName + "\n\n" +
            "Note: Default values have been set for birth date, rates, and allowances.\n" +
            "You can update these through the CSV file if needed.",
            "Employee Added Successfully",
            JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }
    
    // Validation methods for government numbers
    private boolean isValidSSS(String sss) {
        // Basic SSS format validation (allows various formats)
        // Common formats: 12-3456789-0, 123456789, 12 3456789 0
        String cleaned = sss.replaceAll("[\\s-]", ""); // Remove spaces and dashes
        return cleaned.matches("\\d{10}") && cleaned.length() == 10;
    }
    
    private boolean isValidPhilHealth(String philHealth) {
        // Basic PhilHealth format validation
        // Common formats: 12-345678901-2, 123456789012
        String cleaned = philHealth.replaceAll("[\\s-]", ""); // Remove spaces and dashes
        return cleaned.matches("\\d{12}") && cleaned.length() == 12;
    }
    
    private boolean isValidTIN(String tin) {
        // Basic TIN format validation
        // Common formats: 123-456-789-000, 123456789000
        String cleaned = tin.replaceAll("[\\s-]", ""); // Remove spaces and dashes
        return cleaned.matches("\\d{12}") && cleaned.length() == 12;
    }
    
    private boolean isValidPagIbig(String pagIbig) {
        // Basic Pag-IBIG format validation
        // Common formats: 1234-5678-9012, 123456789012
        String cleaned = pagIbig.replaceAll("[\\s-]", ""); // Remove spaces and dashes
        return cleaned.matches("\\d{12}") && cleaned.length() == 12;
    }
    
    private boolean isDuplicateGovernmentNumber(String sss, String philHealth, String tin, String pagIbig) {
        // Check against existing employees for duplicate government numbers
        List<Employee> existingEmployees = parent.getEmployeesList();
        
        for (Employee emp : existingEmployees) {
            if (emp.sssNumber.equals(sss)) {
                JOptionPane.showMessageDialog(this, 
                    "SSS Number '" + sss + "' already exists for employee: " + emp.empFullName + 
                    "\nPlease enter a different SSS Number.",
                    "Duplicate SSS Number",
                    JOptionPane.WARNING_MESSAGE);
                txtSSS.requestFocus();
                txtSSS.selectAll();
                return true;
            }
            
            if (emp.philHealthNumber.equals(philHealth)) {
                JOptionPane.showMessageDialog(this, 
                    "PhilHealth Number '" + philHealth + "' already exists for employee: " + emp.empFullName + 
                    "\nPlease enter a different PhilHealth Number.",
                    "Duplicate PhilHealth Number",
                    JOptionPane.WARNING_MESSAGE);
                txtPhilHealth.requestFocus();
                txtPhilHealth.selectAll();
                return true;
            }
            
            if (emp.tinNumber.equals(tin)) {
                JOptionPane.showMessageDialog(this, 
                    "TIN '" + tin + "' already exists for employee: " + emp.empFullName + 
                    "\nPlease enter a different TIN.",
                    "Duplicate TIN",
                    JOptionPane.WARNING_MESSAGE);
                txtTIN.requestFocus();
                txtTIN.selectAll();
                return true;
            }
            
            if (emp.pagIbigNumber.equals(pagIbig)) {
                JOptionPane.showMessageDialog(this, 
                    "Pag-IBIG Number '" + pagIbig + "' already exists for employee: " + emp.empFullName + 
                    "\nPlease enter a different Pag-IBIG Number.",
                    "Duplicate Pag-IBIG Number",
                    JOptionPane.WARNING_MESSAGE);
                txtPagIbig.requestFocus();
                txtPagIbig.selectAll();
                return true;
            }
        }
        
        return false; // No duplicates found
    }
}

// Employee Class
class Employee {
    private static final Logger logger = Logger.getLogger(Employee.class.getName());
    String empId, lastName, firstName, empFullName, birthDate;
    String sssNumber, philHealthNumber, tinNumber, pagIbigNumber;
    double hourlyRate, basicSalary, riceSubsidy, phoneAllowance, clothingAllowance;
    List<Attendance> attendanceList = new ArrayList<>();
    Map<Integer, Map<Integer, WeeklyDetail>> monthlyWeeklyDetails = new TreeMap<>();
    
    Employee(String empId, String lastName, String firstName, String birthDate, double hourlyRate, 
             double basicSalary, double riceSubsidy, double phoneAllowance, double clothingAllowance,
             String sssNumber, String philHealthNumber, String tinNumber, String pagIbigNumber) {
        this.empId = empId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.empFullName = lastName + ", " + firstName;
        this.birthDate = birthDate;
        this.hourlyRate = hourlyRate;
        this.basicSalary = basicSalary;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
        this.sssNumber = sssNumber;
        this.philHealthNumber = philHealthNumber;
        this.tinNumber = tinNumber;
        this.pagIbigNumber = pagIbigNumber;
    }
    
    void addAttendance(String date, String login, String logout) {
        attendanceList.add(new Attendance(date, login, logout, hourlyRate));
    }
    
    void calculatePayroll() {
        // Use flexible date parsing for different formats
        for (Attendance att : attendanceList) {
            try {
                Date dt = parseFlexibleDate(att.date);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dt);
                int month = cal.get(Calendar.MONTH) + 1;
                int week = cal.get(Calendar.WEEK_OF_YEAR);
                
                att.calculateHoursWorked();
                
                Map<Integer, WeeklyDetail> weekMap = monthlyWeeklyDetails.getOrDefault(month, new TreeMap<>());
                WeeklyDetail wd = weekMap.getOrDefault(week, new WeeklyDetail());
                
                wd.regularHours += att.getRegularHours();
                if (att.getEligibleOvertimePay() > 0) {
                    wd.overtimeHours += 1;
                }
                
                double dailyNet = att.dailyPay + att.getEligibleOvertimePay();
                wd.weeklySalary += dailyNet;
                wd.totalEligibleOvertimePay += att.getEligibleOvertimePay();
                weekMap.put(week, wd);
                monthlyWeeklyDetails.put(month, weekMap);
            } catch (ParseException e) {
                logger.log(Level.SEVERE, "Error parsing date for attendance: {0}", att.date);
            }
        }
        
        // Divide monthly benefits evenly among the weeks in each month
        for (Integer month : monthlyWeeklyDetails.keySet()) {
            Map<Integer, WeeklyDetail> weekMap = monthlyWeeklyDetails.get(month);
            int numWeeks = weekMap.size();
            if (numWeeks == 0) continue;
            double totalBenefits = riceSubsidy + phoneAllowance + clothingAllowance;
            double weeklyBenefit = totalBenefits / numWeeks;
            for (WeeklyDetail wd : weekMap.values()) {
                wd.weeklySalary += weeklyBenefit;
            }
        }
    }
    
    private Date parseFlexibleDate(String dateStr) throws ParseException {
        // Handle different date formats like "6/3/24", "6/3/2024", "06/03/24", etc.
        String[] dateParts = dateStr.split("/");
        if (dateParts.length != 3) {
            throw new ParseException("Invalid date format: " + dateStr, 0);
        }
        
        int month = Integer.parseInt(dateParts[0].trim());
        int day = Integer.parseInt(dateParts[1].trim());
        String yearPart = dateParts[2].trim();
        
        int year;
        if (yearPart.length() == 2) {
            // Handle 2-digit year (e.g., "24" for 2024)
            int shortYear = Integer.parseInt(yearPart);
            // Assume years 00-30 are 2000-2030, and 31-99 are 1931-1999
            if (shortYear <= 30) {
                year = 2000 + shortYear;
            } else {
                year = 1900 + shortYear;
            }
        } else if (yearPart.length() == 4) {
            // Handle 4-digit year (e.g., "2024")
            year = Integer.parseInt(yearPart);
        } else {
            throw new ParseException("Invalid year format: " + yearPart, 0);
        }
        
        // Create a date using Calendar to handle the parsed components
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day); // Calendar months are 0-based
        return cal.getTime();
    }
}

// WeeklyDetail Class
class WeeklyDetail {
    int regularHours = 0;
    int overtimeHours = 0;
    double totalEligibleOvertimePay = 0;
    double weeklySalary = 0;
}

// Attendance Class
class Attendance {
    String date, loginTime, logoutTime;
    boolean isLate;
    double dailyPay, overtimePay;
    double hourlyRate;
    int workHours = 0;
    
    Attendance(String date, String loginTime, String logoutTime, double hourlyRate) {
        this.date = date;
        this.loginTime = loginTime;
        this.logoutTime = logoutTime;
        this.hourlyRate = hourlyRate;
    }
    
    void calculateHoursWorked() {
        LocalTime expectedLogin = EmployeePayrollGUI.EXPECTED_LOGIN;
        LocalTime expectedLogout = EmployeePayrollGUI.EXPECTED_LOGOUT;
        LocalTime loginThreshold = expectedLogin.plusMinutes(EmployeePayrollGUI.GRACE_PERIOD_MINUTES);
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        
        LocalTime actualLogin = LocalTime.parse(this.loginTime, timeFormatter);
        LocalTime actualLogout = LocalTime.parse(this.logoutTime, timeFormatter);
        
        isLate = actualLogin.isAfter(loginThreshold);
        
        Duration workedDuration = calculateWorkedDuration(actualLogin, actualLogout, expectedLogout);
        long workedHours = workedDuration.toHours();
        
        if (workedHours > 1) {
            workedHours -= 1; // Deduct 1 hour for lunch
        }
        if (workedHours < 0) {
            workedHours = 0;
        }
        this.workHours = (int) workedHours;
        
        this.dailyPay = this.hourlyRate * workedHours;
        this.overtimePay = calculateOvertimePay(actualLogout, expectedLogout);
    }
    
    private Duration calculateWorkedDuration(LocalTime actualLogin, LocalTime actualLogout, LocalTime expectedLogout) {
        if (actualLogout.isAfter(expectedLogout)) {
            return Duration.between(actualLogin, expectedLogout);
        } else {
            return Duration.between(actualLogin, actualLogout);
        }
    }
    
    private double calculateOvertimePay(LocalTime actualLogout, LocalTime expectedLogout) {
        if (!isLate && actualLogout.isAfter(expectedLogout)) {
            return this.hourlyRate * 0.25;
        }
        return 0;
    }
    
    int getRegularHours() {
        return workHours;
    }
    
    double getEligibleOvertimePay() {
        boolean eligible = !isLate && LocalTime.parse(logoutTime, DateTimeFormatter.ofPattern("H:mm")).isAfter(LocalTime.of(17, 0));
        if (eligible) {
            return this.hourlyRate * 0.25;
        }
        return 0;
    }
}