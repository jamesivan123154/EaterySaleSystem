package eaterysalesystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class LogInPage extends JFrame {

    private JLabel lblHeader, lblGuide;
    private JLabel lblFacilitatorID, lblPassword;
    private JButton btnContinue;
    private JTextField txtFacilitatorID;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    // Database credentials
    private static final String DB_URL  = "jdbc:postgresql://localhost:5432/eaterydb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASS = "admin123";

    LogInPage() {
        setTitle("Jomar's Eatery – Login");
        setSize(600, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(204, 229, 255));

        // Header
        lblHeader = new JLabel("WELCOME TO JOMAR'S EATERY", SwingConstants.CENTER);
        lblHeader.setBounds(0, 150, 600, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblHeader);

        // Guide
        lblGuide = new JLabel("Log-in to continue", SwingConstants.CENTER);
        lblGuide.setBounds(0, 180, 600, 30);
        lblGuide.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblGuide);

        // Facilitator ID
        lblFacilitatorID = new JLabel("Facilitator ID:");
        lblFacilitatorID.setBounds(180, 220, 110, 25);
        lblFacilitatorID.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblFacilitatorID);

        txtFacilitatorID = new JTextField();
        txtFacilitatorID.setBounds(295, 220, 120, 25);
        add(txtFacilitatorID);

        // Password
        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(180, 255, 110, 25);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(295, 255, 120, 25);
        add(txtPassword);

        // Role dropdown
        cmbRole = new JComboBox<>(new String[]{
            "Continue as Owner",
            "Continue as Staff"
        });
        cmbRole.setBounds(210, 292, 170, 30);
        cmbRole.setBackground(Color.WHITE);
        cmbRole.setForeground(new Color(128, 0, 0));
        cmbRole.setFocusable(false);
        add(cmbRole);

        // Continue button
        btnContinue = new JButton("Continue");
        btnContinue.setBounds(235, 335, 110, 30);
        btnContinue.setFont(new Font("Arial", Font.PLAIN, 12));
        btnContinue.setBackground(new Color(128, 0, 0));
        btnContinue.setForeground(Color.WHITE);
        btnContinue.setFocusPainted(false);
        add(btnContinue);

        // Continue button action
        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        // Logo
        try {
            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/eaterysalesystem/logo.png"));
            Image img = icon.getImage();
            Image resized = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            ImageIcon resizedIcon = new ImageIcon(resized);
            JLabel lblLogo = new JLabel(resizedIcon);
            lblLogo.setBounds(240, 20, 120, 120);
            add(lblLogo);
        } catch (Exception ex) {
            // Logo not found, skip
        }
    }

    private void handleLogin() {
        String enteredID   = txtFacilitatorID.getText().trim();
        String enteredPass = new String(txtPassword.getPassword()).trim();
        String selectedRole = (String) cmbRole.getSelectedItem();

        // Basic validation — fields must not be empty
        if (enteredID.isEmpty() || enteredPass.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please fill in all fields.",
                "Empty Fields",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Determine which role to check in the database
        String roleToCheck;
        if (selectedRole.equals("Continue as Owner")) {
            roleToCheck = "Owner";
        } else {
            roleToCheck = "Staff";
        }

        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql;

            if (roleToCheck.equals("Owner")) {
                // Owner: must have role = 'Owner' exactly
                sql = "SELECT * FROM facilitator " +
                      "WHERE facilitator_id = ? " +
                      "AND password = ? " +
                      "AND role = 'Owner' " +
                      "AND isActive = TRUE";
            } else {
                // Staff: any role can log in as staff (including Owner)
                sql = "SELECT * FROM facilitator " +
                      "WHERE facilitator_id = ? " +
                      "AND password = ? " +
                      "AND isActive = TRUE";
            }

            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(enteredID));
            pst.setString(2, enteredPass);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // Login successful — get actual role from DB
                String actualRole = rs.getString("role");

                rs.close();
                pst.close();
                conn.close();

                // Open landing page with actual role
                EaterySalesSystemLandingPage landingPage =
                        new EaterySalesSystemLandingPage(actualRole);
                landingPage.setVisible(true);
                dispose();

            } else {
                rs.close();
                pst.close();
                conn.close();

                JOptionPane.showMessageDialog(
                    this,
                    "Invalid credentials or you don't have access for this role.\nPlease try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
                );
                txtFacilitatorID.setText("");
                txtPassword.setText("");
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                this,
                "Facilitator ID must be a number.",
                "Invalid Input",
                JOptionPane.WARNING_MESSAGE
            );
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Database connection failed:\n" + ex.getMessage(),
                "Connection Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}