package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The personal learning path of one student for one career.
 *
 * It groups the student's skills into Beginner, Intermediate and Advanced levels
 * using a HashMap: key = level name, value = list of skills in that level.
 */
public class LearningPath {

    private final String studentEmail;
    private final Career career;
    // LinkedHashMap is a HashMap that also remembers insertion order (Beginner first)
    private final Map<String, List<Skill>> skillsByLevel = new LinkedHashMap<>();

    public LearningPath(String studentEmail, Career career, List<Skill> skills) {
        this.studentEmail = studentEmail;
        this.career = career;
        skillsByLevel.put(Skill.BEGINNER, new ArrayList<>());
        skillsByLevel.put(Skill.INTERMEDIATE, new ArrayList<>());
        skillsByLevel.put(Skill.ADVANCED, new ArrayList<>());
        for (Skill skill : skills) {
            skillsByLevel.computeIfAbsent(skill.getLevel(), key -> new ArrayList<>()).add(skill);
        }
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Career getCareer() {
        return career;
    }

    public List<Skill> getSkillsForLevel(String level) {
        List<Skill> skills = skillsByLevel.get(level);
        return skills == null ? new ArrayList<>() : skills;
    }

    public List<String> getLevels() {
        return new ArrayList<>(skillsByLevel.keySet());
    }

    /**
     * Returns every skill in roadmap order (Beginner, then Intermediate, then Advanced).
     */
    public List<Skill> getAllSkills() {
        List<Skill> all = new ArrayList<>();
        for (List<Skill> levelSkills : skillsByLevel.values()) {
            all.addAll(levelSkills);
        }
        return all;
    }

    public Skill findSkill(String skillId) {
        for (Skill skill : getAllSkills()) {
            if (skill.getSkillId().equals(skillId)) {
                return skill;
            }
        }
        return null;
    }

    /**
     * The "next" skill to learn = the first skill in roadmap order that is not completed.
     * In-progress skills are preferred so the dashboard shows what the student is busy with.
     */
    public Skill getNextSkill() {
        for (Skill skill : getAllSkills()) {
            if (skill.getStatus() == SkillStatus.IN_PROGRESS) {
                return skill;
            }
        }
        for (Skill skill : getAllSkills()) {
            if (!skill.isCompleted()) {
                return skill;
            }
        }
        return null;
    }
}
