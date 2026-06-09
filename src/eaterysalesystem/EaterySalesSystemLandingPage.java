/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eaterysalesystem;


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

/**
 *
 * @author Mark Samia
 */
public class EaterySalesSystemLandingPage extends JFrame {

    // ── Colors (matching LogInPage theme exactly) ──
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

    // ── Categories ──
    private static final String[] CATEGORIES = {
        "Popular", "Niche / Acquired"
    };

    // ── Menu Data ──
    public static final ArrayList<String[]> POPULAR_ITEMS =
        new ArrayList<>(Arrays.asList(
                new String[]{"Adobo", "78"},
                new String[]{"Sinigang", "95"},
                new String[]{"Lumpiang Shanghai", "55"},
                new String[]{"Pork Menudo", "85"},
                new String[]{"Tortang Talong", "50"},
                new String[]{"Fried Fish", "60"},
                new String[]{"Tinolang Manok", "90"},
                new String[]{"Chopsuey", "70"},
                new String[]{"Inihaw na Liempo", "110"},
                new String[]{"Pork Chop", "95"},
                new String[]{"Lechon Kawali", "120"},
                new String[]{"Ginataang Kalabasa", "65"},
                new String[]{"Daing na Bangus", "75"},
                new String[]{"Sarsiadong Isda", "70"},
                new String[]{"Beef Pares", "105"},
                new String[]{"Chicken Afritada", "90"},
                new String[]{"Pork Higado", "85"},
                new String[]{"Ginisang Sayote", "55"},
                new String[]{"Beef Caldereta", "115"},
                new String[]{"Lumpiang Togue", "50"}
        ));

    public static final ArrayList<String[]> NICHE_ITEMS =
                new ArrayList<>(Arrays.asList(
                new String[]{"Dinuguan",         "80"},
                new String[]{"Papaitan",         "85"},
                new String[]{"Bopis",            "75"},
                new String[]{"Pinakbet",         "70"},
                new String[]{"Ginisang Monggo",  "60"},
                new String[]{"Laing",            "75"},
                new String[]{"Kare-Kare",        "130"},
                new String[]{"Kinilaw",          "90"},
                new String[]{"Ginataang Langka", "65"},
                new String[]{"Adobong Pusit",    "95"},
                new String[]{"Ginataang Tulinan","85"},
                new String[]{"Ginataang Suso",   "70"},
                new String[]{"Paksiw na Bangus", "65"},
                new String[]{"Sinanglay",        "80"},
                new String[]{"KBL",              "95"},
                new String[]{"Adobong Atay",     "70"},
                new String[]{"Paksiw na Pata",   "105"},
                new String[]{"Balbacua",         "120"},
                new String[]{"Ginataang Santol", "75"},
                new String[]{"Adobong Kamansi",  "80"}
        ));

    // ── State Management ──
    private int selectedCategory = 0;
    private final Map<String, Integer> orderMap = new LinkedHashMap<>();
    private final Map<String, Integer> priceMap = new HashMap<>();

    // ── UI References ──
    private JPanel    pnlItems;
    private JPanel    pnlOrderList;
    private JLabel    lblTotal;
    private JButton[] catButtons;
	private String role;

    // ─────────────────────────────────────────────────────────────
    EaterySalesSystemLandingPage(String role) {
    this.role = role;

    buildPriceLookup();
    initUI();
}

    // ── Build price map for quick lookup ──
    private void buildPriceLookup() {

    priceMap.clear();

    for (String[] row : POPULAR_ITEMS)priceMap.put(row[0], Integer.parseInt(row[1]));
    for (String[] row : NICHE_ITEMS)priceMap.put(row[0], Integer.parseInt(row[1]));
}

    // ── Initialize main UI ──
    private void initUI() {
        setTitle("Jomar's Eatery – Kiosk");
        setSize(860, 640); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_MAIN);

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildCenter(),  BorderLayout.CENTER);
        add(buildSidebar(), BorderLayout.EAST);
    }

    // ─────────────────────────────────────────────────────────────
    // HEADER
    // ─────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setBackground(COLOR_PRIMARY);
        hdr.setPreferredSize(new Dimension(860, 60));
        hdr.setBorder(new EmptyBorder(0, 20, 0, 20));

        JLabel logo = new JLabel("JOMAR'S EATERY");
        logo.setFont(FONT_TITLE);
        logo.setForeground(Color.WHITE);
        hdr.add(logo, BorderLayout.WEST);

        hdr.add(logo, BorderLayout.WEST);

JButton btnLogout = new JButton("Logout");
btnLogout.setBackground(COLOR_ACCENT);
btnLogout.setForeground(Color.BLACK);
btnLogout.setFocusPainted(false);

btnLogout.addActionListener(e -> {
    dispose(); // closes current frame

    // open login page again
    new LogInPage().setVisible(true);
});

JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
rightPanel.setOpaque(false);

JLabel tagline = new JLabel("Lutong Bahay, Presyong Mababa | Order Here");
tagline.setFont(FONT_BODY);
tagline.setForeground(new Color(255, 220, 220));

btnLogout.setPreferredSize(new Dimension(110, 32));

rightPanel.add(tagline);
rightPanel.add(btnLogout);

hdr.add(rightPanel, BorderLayout.EAST);

        return hdr;
    }

    // ─────────────────────────────────────────────────────────────
    // CENTER (greeting + category tabs + items grid)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(BG_MAIN);
        center.add(buildGreeting(),    BorderLayout.NORTH);
        center.add(buildCategoryBar(), BorderLayout.CENTER);
        return center;
    }

    private JPanel buildGreeting() {
    JPanel greet = new JPanel();
    greet.setBackground(BG_MAIN);
    greet.setLayout(new BoxLayout(greet, BoxLayout.Y_AXIS));
    greet.setBorder(new EmptyBorder(14, 20, 6, 20));

    JPanel topRow = new JPanel(new BorderLayout());
    topRow.setBackground(BG_MAIN);

    JLabel hi = new JLabel("Kumain na,");
    hi.setFont(new Font("Arial", Font.BOLD, 22));
    hi.setForeground(COLOR_TEXT);

    topRow.add(hi, BorderLayout.WEST);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
    buttonPanel.setOpaque(false);

    JButton btnEditMenu = new JButton("Edit Menu");
    JButton btnOrders = new JButton("Order History");
    JButton btnSales = new JButton("Sales Report");

    btnEditMenu.setBackground(COLOR_ACCENT);
    btnOrders.setBackground(COLOR_ACCENT);
    btnSales.setBackground(COLOR_ACCENT);

    btnEditMenu.setFocusPainted(false);
    btnOrders.setFocusPainted(false);
    btnSales.setFocusPainted(false);

    btnEditMenu.addActionListener(e -> {
        new EditMenuFrame(this).setVisible(true);
    });
    
    btnOrders.addActionListener(e -> {
    new OrderHistoryFrame().setVisible(true);
    });
	
	btnSales.addActionListener(e -> {
    this.setVisible(false); 
    new SalesReportFrame(this.role, this); 
	});

    buttonPanel.add(btnOrders);

	if (role.equals("Owner")) {
		buttonPanel.add(btnEditMenu);
		buttonPanel.add(btnSales);
	}

    topRow.add(buttonPanel, BorderLayout.EAST);

    JLabel sub = new JLabel("anong gusto mo?");
    sub.setFont(new Font("Arial", Font.PLAIN, 22));
    sub.setForeground(COLOR_TEXT);

    greet.add(topRow);

JPanel subPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
subPanel.setBackground(BG_MAIN);
subPanel.add(sub);

greet.add(subPanel);

    return greet;
}

    private JPanel buildCategoryBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_MAIN);

        // Category tab buttons
        JPanel catBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        catBar.setBackground(BG_MAIN);
        catBar.setBorder(new EmptyBorder(0, 16, 0, 0));
        catButtons = new JButton[CATEGORIES.length];

        for (int i = 0; i < CATEGORIES.length; i++) {
            final int idx = i;
            JButton btn = new JButton(CATEGORIES[i]);
            btn.setFont(FONT_CAT);
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setBorder(new EmptyBorder(6, 18, 6, 18));
            styleCategory(btn, i == selectedCategory);
            btn.addActionListener(e -> switchCategory(idx));
            catButtons[i] = btn;
            catBar.add(btn);
        }

        wrapper.add(catBar, BorderLayout.NORTH);

        // Scrollable items grid
        pnlItems = new JPanel();
        pnlItems.setBackground(BG_MAIN);
        refreshItemsGrid();

        JScrollPane scroll = new JScrollPane(pnlItems);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(BG_MAIN);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private void styleCategory(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(COLOR_PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(180, 215, 245)); 
            btn.setForeground(COLOR_TEXT);
        }
    }

    private void switchCategory(int idx) {
        selectedCategory = idx;
        for (int i = 0; i < catButtons.length; i++) {
            styleCategory(catButtons[i], i == selectedCategory);
        }
        refreshItemsGrid();
    }

    private void refreshItemsGrid() {
        pnlItems.removeAll();
        ArrayList<String[]> items = (selectedCategory == 0) ? POPULAR_ITEMS : NICHE_ITEMS;

        pnlItems.setLayout(new GridLayout(0, 3, 10, 10));
        pnlItems.setBorder(new EmptyBorder(10, 16, 10, 10));

        for (String[] item : items) {
            pnlItems.add(buildItemCard(item[0], item[1]));
        }

        int totalItems = items.size();
        int columns = 3;
        int rows = (int) Math.ceil((double) totalItems / columns);
        int panelHeight = (rows * 140) + ((rows - 1) * 10) + 24;
        pnlItems.setPreferredSize(new Dimension(580, panelHeight));

        pnlItems.revalidate();
        pnlItems.repaint();
    }

    private JPanel buildItemCard(String name, String price) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(COLOR_BORDER, 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Food emoji
        JLabel emoji = new JLabel(getFoodEmoji(name), SwingConstants.CENTER);
        emoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        emoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Item name
        JLabel lblName = new JLabel("<html><center>" + name + "</center></html>", SwingConstants.CENTER);
        lblName.setFont(FONT_SUB);
        lblName.setForeground(COLOR_TEXT);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Price
        JLabel lblPrice = new JLabel("P " + price + ".00", SwingConstants.CENTER);
        lblPrice.setFont(FONT_PRICE);
        lblPrice.setForeground(COLOR_PRIMARY);
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add button
        JButton btnAdd = new JButton("+ Add");
        btnAdd.setFont(new Font("Arial", Font.BOLD, 10));
        btnAdd.setBackground(COLOR_PRIMARY); 
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setBorder(new EmptyBorder(5, 14, 5, 14));
        btnAdd.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);

        final String itemName  = name;
        final int    itemPrice = Integer.parseInt(price);
        
        // Directly adds the item now without any popup menus
        btnAdd.addActionListener(e -> addToOrder(itemName, itemPrice));

        card.add(emoji);
        card.add(Box.createVerticalStrut(4));
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblPrice);
        card.add(Box.createVerticalStrut(8));
        card.add(btnAdd);

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(230, 242, 255));
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_PRIMARY, 2, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_CARD);
                card.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(COLOR_BORDER, 1, true),
                    new EmptyBorder(10, 10, 10, 10)
                ));
            }
        });

        return card;
    }

    // ─────────────────────────────────────────────────────────────
    // SIDEBAR (order summary)
    // ─────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 1, 0, 0, COLOR_BORDER),
            new EmptyBorder(12, 10, 10, 10)
        ));

        // Sidebar title
        JLabel titleOrder = new JLabel("My Order");
        titleOrder.setFont(FONT_TITLE);
        titleOrder.setForeground(COLOR_PRIMARY);

        JLabel titleSub = new JLabel("Dine-In / Take Out");
        titleSub.setFont(FONT_SMALL);
        titleSub.setForeground(COLOR_MUTED);

        JPanel topPanel = new JPanel();
        topPanel.setBackground(BG_SIDEBAR);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(titleOrder);
        topPanel.add(titleSub);
        topPanel.add(Box.createVerticalStrut(10));
        sidebar.add(topPanel, BorderLayout.NORTH);

        // Scrollable order list
        pnlOrderList = new JPanel();
        pnlOrderList.setBackground(BG_SIDEBAR);
        pnlOrderList.setLayout(new BoxLayout(pnlOrderList, BoxLayout.Y_AXIS));

        JScrollPane scrollOrder = new JScrollPane(pnlOrderList);
        scrollOrder.setBorder(null);
        scrollOrder.getViewport().setBackground(BG_SIDEBAR);
        sidebar.add(scrollOrder, BorderLayout.CENTER);

        // Bottom: total + Done button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(BG_SIDEBAR);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

        JSeparator sep = new JSeparator();
        sep.setForeground(COLOR_BORDER);
        bottomPanel.add(sep);
        bottomPanel.add(Box.createVerticalStrut(6));

        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setBackground(BG_SIDEBAR);

        JLabel lblTotalLabel = new JLabel("Total");
        lblTotalLabel.setFont(FONT_SUB);
        lblTotalLabel.setForeground(COLOR_MUTED);

        lblTotal = new JLabel("P 0.00");
        lblTotal.setFont(FONT_TOTAL);
        lblTotal.setForeground(COLOR_PRIMARY);

        totalRow.add(lblTotalLabel, BorderLayout.NORTH);
        totalRow.add(lblTotal,      BorderLayout.SOUTH);
        bottomPanel.add(totalRow);
        bottomPanel.add(Box.createVerticalStrut(10));

        // Done button — Connected to checkout processing dialog
        JButton btnDone = new JButton("Done");
        btnDone.setFont(FONT_BTN);
        btnDone.setBackground(COLOR_ACCENT);
        btnDone.setForeground(new Color(60, 40, 0));
        btnDone.setFocusPainted(false);
        btnDone.setBorder(new EmptyBorder(12, 0, 12, 0));
        btnDone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btnDone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDone.addActionListener(e -> processCheckout());
        bottomPanel.add(btnDone);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);
        return sidebar;
    }

    // ─────────────────────────────────────────────────────────────
    // ORDER LOGIC
    // ─────────────────────────────────────────────────────────────
    private void addToOrder(String name, int price) {
        orderMap.merge(name, 1, Integer::sum);
        refreshOrderPanel();
    }

    private void removeOne(String name) {
        int qty = orderMap.getOrDefault(name, 0);
        if (qty <= 1) orderMap.remove(name);
        else          orderMap.put(name, qty - 1);
        refreshOrderPanel();
    }

    private void processCheckout() {
        if (orderMap.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your order list is empty!", "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate checkout metrics
        int total = orderMap.entrySet().stream()
            .mapToInt(e -> priceMap.getOrDefault(e.getKey(), 0) * e.getValue())
            .sum();

        // Prompt for customer details using JOptionPane input boxes
        String customerName = JOptionPane.showInputDialog(this, 
                "Enter Customer Name to finalize transaction:", 
                "Checkout Summary", JOptionPane.PLAIN_MESSAGE);

        if (customerName == null) return; // Cancel option selected
        if (customerName.trim().isEmpty()) customerName = "Guest Customer";

        // Display success invoice modal
        String message = String.format("Thank you, %s!\nTotal Paid: P %d.00\n\nOrder has been sent to the kitchen.", customerName, total);
        JOptionPane.showMessageDialog(this, message, "Order Successful", JOptionPane.INFORMATION_MESSAGE);
        
        // SAVE TO POSTGRESQL DATABASE
try {
    Connection conn = DriverManager.getConnection(
        "jdbc:postgresql://localhost:5432/eaterydb",
        "postgres",
        "admin123"
    );

    String sql = "INSERT INTO orders(customer_name, total_amount) VALUES (?, ?)";

    PreparedStatement pst = conn.prepareStatement(sql);
    pst.setString(1, customerName);
    pst.setDouble(2, total);

    pst.executeUpdate();

    pst.close();
    conn.close();

} catch (SQLException ex) {
    JOptionPane.showMessageDialog(this,
        "Failed to save order:\n" + ex.getMessage(),
        "Database Error",
        JOptionPane.ERROR_MESSAGE
    );
}

        // Flush application data buffers back to empty states
        orderMap.clear();
        refreshOrderPanel();
    }

    private void refreshOrderPanel() {
        pnlOrderList.removeAll();

        for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
            String item = entry.getKey();
            int    qty  = entry.getValue();
            int    unit = priceMap.getOrDefault(item, 0);

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(BG_SIDEBAR);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            row.setBorder(new EmptyBorder(4, 0, 4, 0));

            JLabel lName = new JLabel("<html><b>" + item + "</b></html>");
            lName.setFont(FONT_SMALL);
            lName.setForeground(COLOR_TEXT);

            JLabel lPrice = new JLabel("P " + unit + ".00");
            lPrice.setFont(FONT_SMALL);
            lPrice.setForeground(COLOR_MUTED);

            JPanel namePanel = new JPanel();
            namePanel.setBackground(BG_SIDEBAR);
            namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
            namePanel.add(lName);
            namePanel.add(lPrice);

            // Quantity controls
            JButton btnMinus = makeQtyBtn("-");
            JLabel  lblQty   = new JLabel(String.valueOf(qty), SwingConstants.CENTER);
            lblQty.setFont(FONT_PRICE);
            lblQty.setPreferredSize(new Dimension(22, 22));
            JButton btnPlus  = makeQtyBtn("+");

            final String itemName  = item;
            final int    itemPrice = unit;
            btnMinus.addActionListener(e -> removeOne(itemName));
            btnPlus.addActionListener(e  -> addToOrder(itemName, itemPrice));

            JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
            qtyPanel.setBackground(BG_SIDEBAR);
            qtyPanel.add(btnMinus);
            qtyPanel.add(lblQty);
            qtyPanel.add(btnPlus);

            row.add(namePanel, BorderLayout.CENTER);
            row.add(qtyPanel,  BorderLayout.EAST);

            JSeparator s = new JSeparator();
            s.setForeground(COLOR_BORDER);

            JPanel wrapper = new JPanel();
            wrapper.setBackground(BG_SIDEBAR);
            wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
            wrapper.add(row);
            wrapper.add(s);
            wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            pnlOrderList.add(wrapper);
        }

        pnlOrderList.revalidate();
        pnlOrderList.repaint();

        // Recalculate total
        int total = orderMap.entrySet().stream()
            .mapToInt(e -> priceMap.getOrDefault(e.getKey(), 0) * e.getValue())
            .sum();
        lblTotal.setText("P " + total + ".00");
    }

    private JButton makeQtyBtn(String label) {
        JButton btn = new JButton(label);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setPreferredSize(new Dimension(24, 24));
        btn.setBackground(new Color(240, 245, 255));
        btn.setForeground(COLOR_TEXT);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(COLOR_BORDER, 1, true));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    public void updateMenuItem(int rowIndex, String newFoodName, int newPrice) {

    if(rowIndex < POPULAR_ITEMS.size()){

        POPULAR_ITEMS.get(rowIndex)[0] = newFoodName;
        POPULAR_ITEMS.get(rowIndex)[1] = String.valueOf(newPrice);

    } else {

        int nicheIndex =
                rowIndex - POPULAR_ITEMS.size();

        NICHE_ITEMS.get(nicheIndex)[0] = newFoodName;
        NICHE_ITEMS.get(nicheIndex)[1] = String.valueOf(newPrice);
    }

    buildPriceLookup();

    refreshItemsGrid();
    refreshOrderPanel();
}
    
    public void addMenuItem(String category, String foodName, int price) {

    String[] newItem = {
        foodName,
        String.valueOf(price)
    };

    if(category.equals("Popular")) {
        POPULAR_ITEMS.add(newItem);
    } else {
        NICHE_ITEMS.add(newItem);
    }

    buildPriceLookup();
    refreshItemsGrid();
}

    // ─────────────────────────────────────────────────────────────
    // FOOD EMOJIS
    // ─────────────────────────────────────────────────────────────
    private String getFoodEmoji(String name) {
        switch (name) {
            case "Adobo":              return "🍖";
            case "Sinigang":           return "🍲";
            case "Lumpiang Shanghai":  return "🥢";
            case "Pork Menudo":        return "🥘";
            case "Tortang Talong":     return "🍳";
            case "Fried Fish":         return "🐟";
            case "Tinolang Manok":     return "🍗";
            case "Chopsuey":           return "🥦";
            case "Inihaw na Liempo":   return "🥩";
            case "Pork Chop":          return "🍽";
            case "Lechon Kawali":      return "🐷";
            case "Ginataang Kalabasa": return "🎃";
            case "Daing na Bangus":    return "🐠";
            case "Sarsiadong Isda":    return "🐡";
            case "Beef Pares":         return "🥣";
            case "Chicken Afritada":   return "🍛";
            case "Pork Higado":        return "🫕";
            case "Ginisang Sayote":    return "🥬";
            case "Beef Caldereta":     return "🫙";
            case "Lumpiang Togue":     return "🌯";
            case "Dinuguan":           return "🩸";
            case "Papaitan":           return "🐐";
            case "Bopis":              return "🫀";
            case "Pinakbet":           return "🌿";
            case "Ginisang Monggo":    return "🫘";
            case "Laing":              return "🌱";
            case "Kare-Kare":          return "🥜";
            case "Kinilaw":            return "🐟";
            case "Ginataang Langka":   return "🫙";
            case "Adobong Pusit":      return "🦑";
            case "Ginataang Tulinan":  return "🐟";
            case "Ginataang Suso":     return "🐌";
            case "Paksiw na Bangus":   return "🐠";
            case "Sinanglay":          return "🐟";
            case "KBL":                return "🍖";
            case "Adobong Atay":       return "🫀";
            case "Paksiw na Pata":     return "🦵";
            case "Balbacua":           return "🐄";
            case "Ginataang Santol":   return "🍈";
            case "Adobong Kamansi":    return "🌰";
            default:                   return "🍽";
        }
    }
}
