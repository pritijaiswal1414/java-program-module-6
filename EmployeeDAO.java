package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    private final String url =
            "jdbc:mysql://localhost:3306/company_db";

    private final String username = "root";
    private final String password = "9279209241";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Save employee details in MySQL
    public void addEmployee(int id, String name,
                            String department, double salary)
            throws SQLException {

        String sql = "INSERT INTO employees " +
                "(emp_id, name, department, salary) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, department);
            ps.setDouble(4, salary);

            ps.executeUpdate();
        }
    }

    // Retrieve all employee records from MySQL
    public List<Object[]> getAllEmployees() throws SQLException {

        List<Object[]> employees = new ArrayList<>();

        String sql = "SELECT emp_id, name, department, salary " +
                "FROM employees ORDER BY emp_id";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] employee = {
                        rs.getInt("emp_id"),
                        rs.getString("name"),
                        rs.getString("department"),
                        rs.getDouble("salary")
                };

                employees.add(employee);
            }
        }

        return employees;
    }
}