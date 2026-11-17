package gui;

import gui.components.ModernButton;
import gui.components.UIFactory;
import service.AppServices;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;

/**
 * MODULE 1: Welcome screen - the first window the user sees.
 */
public class WelcomeFrame extends BaseFrame {

    public WelcomeFrame(AppServices services) {
        super("Welcome", services, 1100, 680);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        JPanel brand = createBrandPanel(
                "Plan your career.\nLearn step by step.\nTrack every skill.",
                "Choose the career you want, get a structured Beginner \u2192 Intermediate \u2192 Advanced "
                        + "roadmap of the skills you need, set personal goals and watch your progress grow.",
                new String[] {
                        "7 career paths with ready-made skill roadmaps",
                        "Track each skill: Not Started, In Progress, Completed",
                        "Personal learning goals with target dates",
                        "Dashboard with live progress statistics"
                });
        add(brand, BorderLayout.CENTER);

        // ---------- right side: actions ----------
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(UIConstants.BACKGROUND);
        right.setPreferredSize(new Dimension(440, 0));

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setPreferredSize(new Dimension(330, 380));

        JLabel title = UIFactory.label("Welcome", UIConstants.FONT_TITLE, UIConstants.TEXT);
        JTextArea subtitle = createWrappedText(
                "Your personal planner for building job-ready skills. Start by creating a free "
                        + "account, or log in to continue your learning path.",
                UIConstants.FONT_BODY, UIConstants.TEXT_SECONDARY);

        JButton getStartedButton = new ModernButton("Get Started  \u2192");
        JButton loginButton = new ModernButton("Login", ModernButton.Style.SECONDARY);

        JPanel registerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        registerRow.setOpaque(false);
        registerRow.add(UIFactory.secondary("New student?  "));
        JButton registerButton = UIFactory.linkButton("Create an account (Register)");
        registerRow.add(registerButton);

        for (JButton button : new JButton[] {getStartedButton, loginButton}) {
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        }
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        registerRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        box.add(title);
        box.add(Box.createVerticalStrut(10));
        box.add(subtitle);
        box.add(Box.createVerticalStrut(32));
        box.add(getStartedButton);
        box.add(Box.createVerticalStrut(12));
        box.add(loginButton);
        box.add(Box.createVerticalStrut(18));
        box.add(registerRow);
        box.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        right.add(box);
        add(right, BorderLayout.EAST);

        // ---------- event handling ----------
        // "Get Started" is for first-time users, so it opens the registration form
        getStartedButton.addActionListener(e -> switchTo(new RegisterFrame(services)));
        loginButton.addActionListener(e -> switchTo(new LoginFrame(services)));
        registerButton.addActionListener(e -> switchTo(new RegisterFrame(services)));
    }
}
