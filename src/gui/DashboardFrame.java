package gui;

import gui.components.ModernButton;
import gui.components.SidebarButton;
import gui.components.UIFactory;
import model.Student;
import service.AppServices;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MODULE 4 window: the main application window after login.
 *
 * Left: dark sidebar with navigation buttons.
 * Right: a CardLayout that shows one screen at a time (Dashboard, Career, Roadmap, ...),
 * so the user switches screens without opening new windows.
 */
public class DashboardFrame extends BaseFrame {

    public static final String HOME = "Dashboard";
    public static final String CAREER = "Choose Career";
    public static final String ROADMAP = "Learning Roadmap";
    public static final String PROGRESS = "My Progress";
    public static final String GOALS = "My Goals";
    public static final String PROFILE = "My Profile";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    // HashMaps connect each screen name to its panel and its sidebar button
    private final Map<String, BasePanel> panels = new LinkedHashMap<>();
    private final Map<String, SidebarButton> navButtons = new LinkedHashMap<>();

    private JLabel userNameLabel;
    private JLabel userEmailLabel;

    public DashboardFrame(AppServices services) {
        super("Dashboard", services, 1320, 820);
        setMinimumSize(new Dimension(1100, 680));
        buildUI();
        showPanel(HOME);
    }

    public AppServices getServices() {
        return services;
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Polymorphism: every screen is stored as a BasePanel, but each one is a different class
        panels.put(HOME, new HomePanel(this));
        panels.put(CAREER, new CareerSelectionPanel(this));
        panels.put(ROADMAP, new LearningRoadmapPanel(this));
        panels.put(PROGRESS, new ProgressPanel(this));
        panels.put(GOALS, new GoalManagementPanel(this));
        panels.put(PROFILE, new ProfilePanel(this));

        for (Map.Entry<String, BasePanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }
        contentPanel.setBackground(UIConstants.BACKGROUND);

        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(UIConstants.SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(26, 16, 20, 16));

        // ---- logo ----
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel logo = UIFactory.label("Trajectory", UIConstants.FONT_HEADING, Color.WHITE);
        JLabel tagline = UIFactory.label("Career & Skill Roadmap", UIConstants.FONT_SMALL, UIConstants.SIDEBAR_TEXT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        tagline.setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 0));
        top.add(logo);
        top.add(tagline);
        top.add(Box.createVerticalStrut(28));

        JLabel menuLabel = UIFactory.label("MENU", UIConstants.FONT_SMALL_BOLD, new Color(0x6B6B6B));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 8, 0));
        top.add(menuLabel);

        // ---- navigation buttons ----
        for (String name : panels.keySet()) {
            SidebarButton button = new SidebarButton(name);
            button.addActionListener(e -> showPanel(name));
            navButtons.put(name, button);
            top.add(button);
            top.add(Box.createVerticalStrut(4));
        }
        for (Component c : top.getComponents()) {
            ((javax.swing.JComponent) c).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        sidebar.add(top, BorderLayout.NORTH);

        // ---- current user + logout ----
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        userNameLabel = UIFactory.label("", UIConstants.FONT_SUBHEADING, Color.WHITE);
        userEmailLabel = UIFactory.label("", UIConstants.FONT_SMALL, UIConstants.SIDEBAR_TEXT);
        userNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 2, 0));
        userEmailLabel.setBorder(BorderFactory.createEmptyBorder(0, 6, 12, 0));
        JButton logoutButton = new ModernButton("Logout", ModernButton.Style.DARK);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        logoutButton.addActionListener(e -> logout());
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userEmailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bottom.add(userNameLabel);
        bottom.add(userEmailLabel);
        bottom.add(logoutButton);
        sidebar.add(bottom, BorderLayout.SOUTH);

        refreshUserInfo();
        return sidebar;
    }

    /**
     * Shows the requested screen, highlights its sidebar button and reloads its data.
     */
    public void showPanel(String name) {
        BasePanel panel = panels.get(name);
        if (panel == null) {
            return;
        }
        for (Map.Entry<String, SidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setSelectedItem(entry.getKey().equals(name));
        }
        panel.refreshData();
        cardLayout.show(contentPanel, name);
    }

    /**
     * Updates the name and email shown at the bottom of the sidebar.
     */
    public void refreshUserInfo() {
        Student student = services.getAuthenticationService().getCurrentStudent();
        if (student != null) {
            userNameLabel.setText(student.getFullName());
            userEmailLabel.setText(student.getEmail());
        }
    }

    /**
     * MODULE 11: Logout - clears the session and returns to the login screen.
     * Nothing is deleted; all data stays in the files.
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Do you want to log out?", "Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            services.getAuthenticationService().logout();
            SwingUtilities.invokeLater(() -> switchTo(new LoginFrame(services)));
        }
    }
}
