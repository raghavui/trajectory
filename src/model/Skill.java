package model;

/**
 * One skill inside a career roadmap, for example "Collections Framework".
 */
public class Skill {

    public static final String BEGINNER = "Beginner";
    public static final String INTERMEDIATE = "Intermediate";
    public static final String ADVANCED = "Advanced";

    private final String skillId;
    private final String skillName;
    private final String description;
    private final String careerName;
    private final String level;
    private final String estimatedDuration;
    private SkillStatus status;

    public Skill(String skillId, String skillName, String description, String careerName,
                 String level, String estimatedDuration) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.description = description;
        this.careerName = careerName;
        this.level = level;
        this.estimatedDuration = estimatedDuration;
        this.status = SkillStatus.NOT_STARTED;   // every new skill starts as "Not Started"
    }

    /**
     * Copy constructor. The career catalogue keeps one "master" copy of each skill;
     * each student gets their own copy so that one student's status never affects another.
     */
    public Skill(Skill original) {
        this(original.skillId, original.skillName, original.description, original.careerName,
                original.level, original.estimatedDuration);
    }

    public String getSkillId() {
        return skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public String getDescription() {
        return description;
    }

    public String getCareerName() {
        return careerName;
    }

    public String getLevel() {
        return level;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    public SkillStatus getStatus() {
        return status;
    }

    public void setStatus(SkillStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Skill status cannot be null");
        }
        this.status = status;
    }

    public boolean isCompleted() {
        return status == SkillStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return skillName + " (" + level + ", " + status.getLabel() + ")";
    }
}
