package model;

import java.util.ArrayList;
import java.util.List;

/**
 * A career option such as "Java Developer", with its description and predefined skills.
 */
public class Career {

    private final String careerName;
    private final String description;
    private final String estimatedDuration;
    private final String skillPrefix;          // used to build skill IDs, e.g. "JAVA"
    private final List<Skill> requiredSkills = new ArrayList<>();

    public Career(String careerName, String description, String estimatedDuration, String skillPrefix) {
        this.careerName = careerName;
        this.description = description;
        this.estimatedDuration = estimatedDuration;
        this.skillPrefix = skillPrefix;
    }

    /**
     * Adds a skill to this career. The skill ID is created automatically
     * from the prefix, the level and a running number, e.g. "JAVA-B03".
     */
    public void addSkill(String level, String skillName, String description, String duration) {
        int numberInLevel = 1;
        for (Skill skill : requiredSkills) {
            if (skill.getLevel().equals(level)) {
                numberInLevel++;
            }
        }
        String skillId = String.format("%s-%c%02d", skillPrefix, level.charAt(0), numberInLevel);
        requiredSkills.add(new Skill(skillId, skillName, description, careerName, level, duration));
    }

    /**
     * Returns the list of learning levels used by this career (in order).
     */
    public List<String> getLearningLevels() {
        List<String> levels = new ArrayList<>();
        for (Skill skill : requiredSkills) {
            if (!levels.contains(skill.getLevel())) {
                levels.add(skill.getLevel());
            }
        }
        return levels;
    }

    public String getCareerName() {
        return careerName;
    }

    public String getDescription() {
        return description;
    }

    public String getEstimatedDuration() {
        return estimatedDuration;
    }

    /**
     * Returns a copy of the list so that outside code cannot change the master list.
     */
    public List<Skill> getRequiredSkills() {
        return new ArrayList<>(requiredSkills);
    }

    public int getSkillCount() {
        return requiredSkills.size();
    }

    @Override
    public String toString() {
        return careerName;
    }
}
