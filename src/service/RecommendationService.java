package service;

import model.Career;
import model.LearningPath;
import model.Skill;
import model.SkillStatus;
import model.Student;
import repository.SkillRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Rule-based skill recommendation system (NOT artificial intelligence).
 *
 * Rules used:
 * 1. The skills recommended for a career are the predefined skills of that career.
 * 2. Skills are learned in order: Beginner -> Intermediate -> Advanced.
 * 3. Skills that are already "In Progress" are recommended first (finish what you started).
 * 4. Then "Not Started" skills from the lowest level that is not yet finished.
 */
public class RecommendationService {

    private final CareerService careerService;
    private final SkillRepository skillRepository;

    public RecommendationService(CareerService careerService, SkillRepository skillRepository) {
        this.careerService = careerService;
        this.skillRepository = skillRepository;
    }

    /**
     * Rule 1: returns the predefined skills of a career (each status = Not Started).
     */
    public List<Skill> getRecommendedSkills(String careerName) {
        List<Skill> result = new ArrayList<>();
        Career career = careerService.getCareer(careerName);
        if (career == null) {
            return result;
        }
        for (Skill masterSkill : career.getRequiredSkills()) {
            result.add(new Skill(masterSkill));   // copy, so the master list is never changed
        }
        return result;
    }

    /**
     * Builds the personal learning path of a student: the career skills
     * with the student's saved status applied to each skill.
     * Returns null if the student has not selected a career yet.
     */
    public LearningPath generateLearningPath(Student student) throws IOException {
        if (!student.hasSelectedCareer()) {
            return null;
        }
        Career career = careerService.getCareer(student.getSelectedCareer());
        if (career == null) {
            return null;
        }
        List<Skill> skills = getRecommendedSkills(career.getCareerName());
        Map<String, SkillStatus> savedStatuses = skillRepository.loadStatuses(student.getEmail());
        for (Skill skill : skills) {
            SkillStatus saved = savedStatuses.get(skill.getSkillId());
            if (saved != null) {
                skill.setStatus(saved);
            }
        }
        return new LearningPath(student.getEmail(), career, skills);
    }

    /**
     * Rules 2-4: returns up to {@code limit} skills the student should focus on next.
     */
    public List<Skill> getNextRecommendedSkills(LearningPath path, int limit) {
        List<Skill> recommended = new ArrayList<>();
        if (path == null) {
            return recommended;
        }
        // Rule 3: skills already in progress
        for (Skill skill : path.getAllSkills()) {
            if (recommended.size() < limit && skill.getStatus() == SkillStatus.IN_PROGRESS) {
                recommended.add(skill);
            }
        }
        // Rule 4: not-started skills, lowest level first (getAllSkills is already in level order)
        for (Skill skill : path.getAllSkills()) {
            if (recommended.size() < limit && skill.getStatus() == SkillStatus.NOT_STARTED) {
                recommended.add(skill);
            }
        }
        return recommended;
    }

    /**
     * Returns the level the student is currently working on:
     * the first level that still has unfinished skills.
     */
    public String getCurrentLevel(LearningPath path) {
        if (path == null) {
            return "-";
        }
        for (String level : path.getLevels()) {
            for (Skill skill : path.getSkillsForLevel(level)) {
                if (!skill.isCompleted()) {
                    return level;
                }
            }
        }
        return "All levels completed";
    }
}
