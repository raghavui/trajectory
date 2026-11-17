package gui;

/**
 * Implemented by every screen inside the dashboard.
 * When the user opens a screen, refreshData() reloads the latest saved data,
 * so the dashboard and progress screens are always up to date.
 */
public interface Refreshable {

    void refreshData();
}
