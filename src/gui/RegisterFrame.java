package gui;

import gui.components.ModernButton;
import gui.components.RoundedPanel;
import gui.components.UIFactory;
import model.Student;
import service.AppServices;
import util.UIConstants;
import util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

/**
 * MODULE 2: Student registration screen.
 */
public class RegisterFrame extends BaseFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JTextField collegeField;
    private JComboBox<String> yearCombo;

    public RegisterFrame(AppServices services) {
        super("Register", services, 1100, 720);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(createBrandPanel("Create your account\nin less than a minute.",
                "Register once and your career choice, skill progress and goals are saved on this computer.",
                new String[] {"Passwords are stored securely as salted SHA-256 hashes",
                        "Each student sees only their own data"}),
                BorderLayout.CENTER);

        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIConstants.BACKGROUND);
        right.setPreferredSize(new Dimension(560, 0));

        RoundedPanel card = new RoundedPanel(null);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(28, 32, 24, 32));
        card.setPreferredSize(new Dimension(470, 600));

        nameField = UIFactory.textField();
        emailField = UIFactory.textField();
        passwordField = UIFactory.passwordField();
        confirmField = UIFactory.passwordField();
        collegeField = UIFactory.textField();
        yearCombo = UIFactory.comboBox(ValidationUtil.ACADEMIC_YEARS);

        JLabel title = UIFactory.label("Student Registration", UIConstants.FONT_TITLE, UIConstants.TEXT);
        JLabel subtitle = UIFactory.secondary("All fields are required");
        JLabel passwordHint = UIFactory.label("At least 6 characters, including a letter and a number",
                UIConstants.FONT_SMALL, UIConstants.TEXT_SECONDARY);

        JButton registerButton = new ModernButton("Create Account");

        JPanel loginRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        loginRow.setOpaque(false);
        loginRow.add(UIFactory.secondary("Already registered?  "));
        JButton loginLink = UIFactory.linkButton("Login");
        loginRow.add(loginLink);
        JButton backLink = UIFactory.linkButton("\u2190  Back to Welcome");

        UIFactory.stackVertically(card, title, Box.createVerticalStrut(2), subtitle, Box.createVerticalStrut(18),
                UIFactory.fieldLabel("Full Name"), nameField, Box.createVerticalStrut(12),
                UIFactory.fieldLabel("Email Address"), emailField, Box.createVerticalStrut(12),
                twoColumns(labelled("Password", passwordField), labelled("Confirm Password", confirmField)),
                Box.createVerticalStrut(4), passwordHint, Box.createVerticalStrut(12),
                twoColumns(labelled("College Name", collegeField), labelled("Current Year", yearCombo)),
                Box.createVerticalStrut(22), registerButton, Box.createVerticalStrut(12),
                loginRow, Box.createVerticalGlue(), backLink);

        right.add(card);
        add(right, BorderLayout.EAST);

        // ---------- event handling ----------
        registerButton.addActionListener(e -> performRegistration());
        getRootPane().setDefaultButton(registerButton);
        loginLink.addActionListener(e -> switchTo(new LoginFrame(services)));
        backLink.addActionListener(e -> switchTo(new WelcomeFrame(services)));
    }

    private JPanel labelled(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(UIFactory.fieldLabel(labelText), BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel twoColumns(JPanel left, JPanel right) {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.add(left);
        row.add(right);
        return row;
    }

    private void performRegistration() {
        try {
            Student student = services.getAuthenticationService().register(
                    nameField.getText(),
                    emailField.getText(),
                    new String(passwordField.getPassword()),
                    new String(confirmField.getPassword()),
                    collegeField.getText(),
                    (String) yearCombo.getSelectedItem());
            showInfo("Registration successful!\nWelcome, " + student.getFirstName()
                    + ". Please log in with your email and password.");
            switchTo(new LoginFrame(services, student.getEmail()));
        } catch (Exception e) {
            handleError(e);
        }
    }
}
