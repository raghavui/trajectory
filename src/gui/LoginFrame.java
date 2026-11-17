package gui;

import gui.components.ModernButton;
import gui.components.RoundedPanel;
import gui.components.UIFactory;
import service.AppServices;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

/**
 * MODULE 3: Student login screen.
 */
public class LoginFrame extends BaseFrame {

    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginFrame(AppServices services) {
        super("Login", services, 1100, 680);
        buildUI();
    }

    /**
     * Constructor overloading: opens the login screen with the email already filled in
     * (used right after a successful registration).
     */
    public LoginFrame(AppServices services, String prefilledEmail) {
        this(services);
        emailField.setText(prefilledEmail);
        passwordField.requestFocusInWindow();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(createBrandPanel("Welcome back!\nLet's continue learning.",
                "Log in to see your dashboard, update your skills and check your goals.",
                new String[] {"Your progress is saved automatically", "Pick up exactly where you left off"}),
                BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIConstants.BACKGROUND);
        right.setPreferredSize(new Dimension(480, 0));

        RoundedPanel card = new RoundedPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(32, 32, 28, 32));
        card.setPreferredSize(new Dimension(380, 440));

        JLabel title = UIFactory.label("Student Login", UIConstants.FONT_TITLE, UIConstants.TEXT);
        JLabel subtitle = UIFactory.secondary("Enter your registered email and password");

        emailField = UIFactory.textField();
        passwordField = UIFactory.passwordField();

        JButton loginButton = new ModernButton("Login");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerRow.setOpaque(false);
        registerRow.add(UIFactory.secondary("Don't have an account?  "));
        JButton registerLink = UIFactory.linkButton("Register");
        registerRow.add(registerLink);

        JButton backLink = UIFactory.linkButton("\u2190  Back to Welcome");

        UIFactory.stackVertically(card, title, Box.createVerticalStrut(4), subtitle, Box.createVerticalStrut(26),
                UIFactory.fieldLabel("Email Address"), emailField, Box.createVerticalStrut(16),
                UIFactory.fieldLabel("Password"), passwordField, Box.createVerticalStrut(26),
                loginButton, Box.createVerticalStrut(16), registerRow, Box.createVerticalGlue(), backLink);

        right.add(card);
        add(right, BorderLayout.EAST);

        // ---------- event handling ----------
        loginButton.addActionListener(e -> performLogin());
        getRootPane().setDefaultButton(loginButton);   // pressing Enter clicks Login
        registerLink.addActionListener(e -> switchTo(new RegisterFrame(services)));
        backLink.addActionListener(e -> switchTo(new WelcomeFrame(services)));
    }

    private void performLogin() {
        try {
            String password = new String(passwordField.getPassword());
            // On success the AuthenticationService remembers this student as the session user
            services.getAuthenticationService().login(emailField.getText(), password);
            switchTo(new DashboardFrame(services));
        } catch (Exception e) {
            passwordField.setText("");
            handleError(e);
        }
    }
}
