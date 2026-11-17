package gui;

import gui.components.ScrollablePanel;
import gui.components.UIFactory;
import model.Student;
import service.AppServices;
import util.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Abstract parent of every screen shown inside the dashboard window.
 *
 * It builds the common page header (title + subtitle) and a scrollable body,
 * and gives easy access to the services and the logged-in student.
 * Each child class must implement refreshData() (from the Refreshable interface).
 */
public abstract class BasePanel extends JPanel implements Refreshable {

    protected final DashboardFrame dashboard;
    protected final ScrollablePanel body;
    private final JLabel titleLabel;
    private final JLabel subtitleLabel;

    protected BasePanel(DashboardFrame dashboard, String title, String subtitle) {
        super(new BorderLayout());
        this.dashboard = dashboard;
        setBackground(UIConstants.BACKGROUND);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(28, 32, 18, 32));
        titleLabel = UIFactory.label(title, UIConstants.FONT_TITLE, UIConstants.TEXT);
        subtitleLabel = UIFactory.secondary(subtitle);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitleLabel);
        add(header, BorderLayout.NORTH);

        body = new ScrollablePanel(null);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));
        add(UIFactory.scrollPane(body), BorderLayout.CENTER);
    }

    protected void setTitle(String title) {
        titleLabel.setText(title);
    }

    protected void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    /**
     * Adds a section to the body with a gap below it.
     */
    protected void addSection(JComponent section) {
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(section);
        body.add(Box.createVerticalStrut(UIConstants.GAP));
    }

    /**
     * Stops a section from stretching taller than it needs to be.
     */
    protected static void limitHeight(JComponent component) {
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component.getPreferredSize().height));
    }

    protected AppServices services() {
        return dashboard.getServices();
    }

    protected Student student() {
        return services().getAuthenticationService().getCurrentStudent();
    }

    protected void handleError(Exception e) {
        dashboard.handleError(e);
    }

    protected void showInfo(String message) {
        dashboard.showInfo(message);
    }

    protected boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(dashboard, message, "Please confirm",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Rebuilds the body after data has changed.
     */
    protected void reloadBody() {
        body.revalidate();
        body.repaint();
    }
}
