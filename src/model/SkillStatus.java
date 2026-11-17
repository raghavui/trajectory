package model;

/**
 * The three possible states of a skill.
 *
 * Using an enum (instead of free text) means an invalid status like "Done!!"
 * can never be stored by mistake.
 */
public enum SkillStatus {

    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed");

    private final String label;

    SkillStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    /**
     * Converts text such as "IN_PROGRESS" or "In Progress" back to the enum value.
     *
     * @throws IllegalArgumentException if the text is not a valid status
     */
    public static SkillStatus fromText(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Skill status cannot be empty");
        }
        String cleaned = text.trim();
        for (SkillStatus status : values()) {
            if (status.name().equalsIgnoreCase(cleaned) || status.label.equalsIgnoreCase(cleaned)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid skill status: " + text);
    }

    @Override
    public String toString() {
        return label;
    }
}
