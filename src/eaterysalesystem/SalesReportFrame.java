//Name: SEDORO, Kirby T.
//Year & Section: BSIT 2-2
//Date: 

package eaterysalesystem;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;


public class SalesReportFrame extends JFrame{

	
	private static final Color BG_MAIN       = new Color(204, 229, 255); // Light blue canvas
    private static final Color BG_SIDEBAR    = new Color(255, 255, 255); // White sidebar
    private static final Color COLOR_PRIMARY = new Color(128, 0, 0);     // Deep red
    private static final Color COLOR_ACCENT  = new Color(255, 193, 7);   // Golden yellow
    private static final Color COLOR_CARD    = new Color(255, 255, 255); // White item cards
    private static final Color COLOR_TEXT    = new Color(30, 30, 30);    // Dark text
    private static final Color COLOR_MUTED   = new Color(100, 110, 130); // Blue-gray muted labels
    private static final Color COLOR_BORDER  = new Color(180, 210, 240); // Soft blue border

    // ── Fonts ──
    private static final Font FONT_TITLE = new Font("Arial", Font.BOLD,  20);
    private static final Font FONT_SUB   = new Font("Arial", Font.BOLD,  13);
    private static final Font FONT_BODY  = new Font("Arial", Font.PLAIN, 11);
    private static final Font FONT_PRICE = new Font("Arial", Font.BOLD,  12);
    private static final Font FONT_TOTAL = new Font("Arial", Font.BOLD,  18);
    private static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 10);
    private static final Font FONT_CAT   = new Font("Arial", Font.BOLD,  11);
    private static final Font FONT_BTN   = new Font("Arial", Font.BOLD,  13);
	
	
	private String role;
	private JFrame parent;
	
	

	
	public SalesReportFrame(String role, JFrame parent) {
        this.role = role;
        this.parent = parent;
        
        setTitle("Sales Report - Jomar's Eatery");
        setSize(860, 640);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(buildHeader(), BorderLayout.NORTH);
        add(buildHeader(), BorderLayout.NORTH);
        add(buildSidebar(), BorderLayout.WEST); // 
        
        setVisible(true);
    }
	
	private JPanel buildHeader() {
    JPanel hdr = new JPanel(new BorderLayout());
    hdr.setBackground(new Color(128, 0, 0)); // Deep Red
    hdr.setPreferredSize(new Dimension(860, 60));
    hdr.setBorder(new EmptyBorder(0, 20, 0, 20));

    // Logo (Left)
    JLabel logo = new JLabel("JOMAR'S EATERY");
    logo.setFont(new Font("Arial", Font.BOLD, 22));
    logo.setForeground(Color.WHITE);
    hdr.add(logo, BorderLayout.WEST);

    // Right side container (Tagline + Logout Button)
    JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
    rightPanel.setOpaque(false);

    // Tagline
    JLabel tagline = new JLabel("Lutong Bahay, Presyong Mababa | Order Here");
    tagline.setFont(new Font("Arial", Font.PLAIN, 12));
    tagline.setForeground(new Color(240, 240, 240));
    rightPanel.add(tagline);

    // Logout Button (Yellow)
    JButton btnExit = new JButton("Exit");
    btnExit.setFont(new Font("Arial", Font.BOLD, 12));
    btnExit.setBackground(new Color(255, 193, 7)); // Golden Yellow
    btnExit.setForeground(Color.BLACK);
    btnExit.setFocusPainted(false);
    btnExit.setPreferredSize(new Dimension(100, 35));
    
    btnExit.addActionListener(e -> {
        dispose();              // Close this report
        parent.setVisible(true); // Show the original landing page
    });
    rightPanel.add(btnExit);

    hdr.add(rightPanel, BorderLayout.EAST);
    return hdr;
}
	private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(BG_MAIN);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create buttons
        JButton btnFood = createSideButton("Sales per Food");
        JButton btnEmp = createSideButton("Sales Per Employee");
        JButton btnTotal = createSideButton("Total Sales");

        sidebar.add(btnFood);
        sidebar.add(Box.createVerticalStrut(10)); // Gap between buttons
        sidebar.add(btnEmp);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnTotal);

        return sidebar;
    }
	
	private JButton createSideButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BTN);
        btn.setBackground(COLOR_PRIMARY); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setMaximumSize(new Dimension(180, 45));
        return btn;
    }
	
}
