package eaterysalesystem;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JLabel lblHeader, lblAdmin, lblPassword, lblGuide;
    private JButton btnContinue;
    private JTextField txtAdmin;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;

    private static final String userID = "admin";
    private static final String password = "123";

    LogInPage() {

        setSize(600, 600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(204, 229, 255));

        lblHeader = new JLabel("WELCOME TO JOMAR'S EATERY", SwingConstants.CENTER);
        lblHeader.setBounds(0, 150, 600, 30);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblHeader);

        lblGuide = new JLabel("Log-in to continue", SwingConstants.CENTER);
        lblGuide.setBounds(0, 180, 600, 30);
        lblGuide.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblGuide);

        lblAdmin = new JLabel("Admin ID:");
        lblAdmin.setBounds(200, 210, 100, 30);
        lblAdmin.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblAdmin);

        txtAdmin = new JTextField();
        txtAdmin.setBounds(280, 210, 100, 25);
        add(txtAdmin);

        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(200, 240, 100, 30);
        lblPassword.setFont(new Font("Arial", Font.PLAIN, 12));
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(280, 240, 100, 25);
        add(txtPassword);

        // Role dropdown
        cmbRole = new JComboBox<>(new String[]{
            "Log in as Owner",
            "Log in as Staff"
        });
        cmbRole.setBounds(220, 280, 160, 30);
		cmbRole.setBackground(Color.WHITE);
		cmbRole.setForeground(new Color(128, 0, 0)); 
		cmbRole.setFocusable(false);
        add(cmbRole);

        // Continue button
        btnContinue = new JButton("Continue");
        btnContinue.setBounds(240, 320, 100, 30);
        btnContinue.setFont(new Font("Arial", Font.PLAIN, 12));
        btnContinue.setBackground(new Color(128, 0, 0));
        btnContinue.setForeground(Color.WHITE);
        add(btnContinue);

        btnContinue.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String enteredID = txtAdmin.getText().trim();
                String enteredPass = new String(txtPassword.getPassword()).trim();
                String selectedRole = (String) cmbRole.getSelectedItem();

                if (enteredID.equals(userID) && enteredPass.equals(password)) {

					String role;

					if (selectedRole.equals("Log in as Owner")) {
						role = "Owner";
					} else {
						role = "Staff";
					}

					EaterySalesSystemLandingPage landingPage =
							new EaterySalesSystemLandingPage(role);

					landingPage.setVisible(true);
					dispose();

                } else {

                    JOptionPane.showMessageDialog(
                            null,
                            "Invalid Admin ID or Password. Please try again.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );

                    txtAdmin.setText("");
                    txtPassword.setText("");
                }
            }
        });

        // Logo
        try {

            ImageIcon icon = new ImageIcon(
                    getClass().getResource("/eaterysalesystem/logo.png"));

            Image img = icon.getImage();
            Image resized = img.getScaledInstance(
                    120, 120, Image.SCALE_SMOOTH);

            ImageIcon resizedIcon = new ImageIcon(resized);

            JLabel lblLogo = new JLabel(resizedIcon);
            lblLogo.setBounds(240, 20, 120, 120);
            add(lblLogo);

        } catch (Exception ex) {
            // Logo not found
        }
    }
}