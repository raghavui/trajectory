package util;

import java.awt.Color;
import java.awt.Font;

/**
 * All colours and fonts used by the user interface, kept in one place
 * so every screen looks consistent.
 */
public final class UIConstants {

    // ---- Main colour palette ----
    public static final Color BACKGROUND = new Color(0xF5F5F2);
    public static final Color SIDEBAR = new Color(0x171717);
    public static final Color PRIMARY = new Color(0x2563EB);
    public static final Color CARD = new Color(0xFFFFFF);
    public static final Color TEXT = new Color(0x222222);
    public static final Color TEXT_SECONDARY = new Color(0x777777);
    public static final Color SUCCESS = new Color(0x16A34A);

    // ---- Supporting colours ----
    public static final Color PRIMARY_DARK = new Color(0x1D4ED8);
    public static final Color PRIMARY_LIGHT = new Color(0xEAF0FE);
    public static final Color SUCCESS_LIGHT = new Color(0xE7F6EC);
    public static final Color WARNING = new Color(0xD97706);
    public static final Color WARNING_LIGHT = new Color(0xFDF3E3);
    public static final Color DANGER = new Color(0xDC2626);
    public static final Color DANGER_LIGHT = new Color(0xFDECEC);
    public static final Color NEUTRAL = new Color(0x8A8A85);
    public static final Color NEUTRAL_LIGHT = new Color(0xEFEFEC);
    public static final Color BORDER = new Color(0xE4E4DF);
    public static final Color SIDEBAR_TEXT = new Color(0xA3A3A3);
    public static final Color SIDEBAR_HOVER = new Color(0x262626);

    // ---- Fonts ----
    private static final String FONT_FAMILY = "Segoe UI";

    public static final Font FONT_TITLE = new Font(FONT_FAMILY, Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font FONT_SUBHEADING = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font FONT_BODY = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font(FONT_FAMILY, Font.PLAIN, 12);
    public static final Font FONT_SMALL_BOLD = new Font(FONT_FAMILY, Font.BOLD, 12);
    public static final Font FONT_STAT = new Font(FONT_FAMILY, Font.BOLD, 28);
    public static final Font FONT_HERO = new Font(FONT_FAMILY, Font.BOLD, 40);

    // ---- Sizes ----
    public static final int RADIUS = 14;
    public static final int GAP = 16;

    private UIConstants() {
    }
}
