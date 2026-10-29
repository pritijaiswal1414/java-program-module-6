package com.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EmployeeForm extends JFrame {

    private JTextField idField;
    private JTextField nameField;
    private JTextField departmentField;
    private JTextField salaryField;

    private JTable employeeTable;
    private DefaultTableModel tableModel;

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public EmployeeForm() {

        setTitle("Employee Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Heading
        JLabel heading = new JLabel(
                "EMPLOYEE MANAGEMENT SYSTEM",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 22));
        heading.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        );

        add(heading, BorderLayout.NORTH);

        // Employee input form
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.setBorder(
                BorderFactory.createTitledBorder("Add New Employee")
        );

        idField = new JTextField();
        nameField = new JTextField();
        departmentField = new JTextField();
        salaryField = new JTextField();

        formPanel.add(new JLabel("Employee ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Department:"));
        formPanel.add(departmentField);

        formPanel.add(new JLabel("Salary:"));
        formPanel.add(salaryField);

        // Buttons
        JButton submitButton = new JButton("Submit");
        JButton viewButton = new JButton("View All Employees");

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 10)
        );

        buttonPanel.add(submitButton);
        buttonPanel.add(viewButton);

        // Top section: form and buttons
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));

        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Employee table columns
        String[] columns = {
                "Employee ID",
                "Name",
                "Department",
                "Salary"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setRowHeight(25);
        employeeTable.setAutoCreateRowSorter(true);
        employeeTable.setFillsViewportHeight(true);

        JScrollPane scrollPane = new JScrollPane(employeeTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder("All Employee Records")
        );

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        contentPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10)
        );

        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Table always visible at the bottom
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Submit button: save and immediately refresh the table
        submitButton.addActionListener(e -> addEmployee());

        // View button: refresh all employee records
        viewButton.addActionListener(e -> loadEmployees());

        // Display application
        setVisible(true);

        // Automatically display existing records when the app opens
        loadEmployees();
    }

    // Add employee to the database
    private void addEmployee() {

        String idText = idField.getText().trim();
        String name = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String salaryText = salaryField.getText().trim();

        if (idText.isEmpty()
                || name.isEmpty()
                || department.isEmpty()
                || salaryText.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all fields.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        try {

            int id = Integer.parseInt(idText);
            double salary = Double.parseDouble(salaryText);

            if (id <= 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee ID must be greater than zero."
                );

                return;
            }

            if (!Double.isFinite(salary) || salary < 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a valid, non-negative salary."
                );

                return;
            }

            // Save employee details to MySQL
            employeeDAO.addEmployee(
                    id,
                    name,
                    department,
                    salary
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Employee added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            // Clear input fields
            idField.setText("");
            nameField.setText("");
            departmentField.setText("");
            salaryField.setText("");

            // Automatically display the new record
            loadEmployees();

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Employee ID must be a whole number and salary must be numeric.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to save employee: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // Load and display all employee records
    private void loadEmployees() {

        try {

            List<Object[]> employees = employeeDAO.getAllEmployees();

            // Remove old table rows
            tableModel.setRowCount(0);

            // Add all records retrieved from MySQL
            for (Object[] employee : employees) {
                tableModel.addRow(employee);
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load employee records: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // Main method
    public static void main(String[] args) {

        SwingUtilities.invokeLater(EmployeeForm::new);
    }
}