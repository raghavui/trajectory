package service;

import model.LearningPath;
import model.Skill;
import model.SkillStatus;
import repository.ActivityRepository;
import repository.SkillRepository;
import util.ValidationException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Updates skill statuses and calculates progress.
 *
 * Formula: Progress Percentage = (Completed Skills / Total Skills) x 100
 */
public class ProgressTracker {

    private final SkillRepository skillRepository;
    private final ActivityRepository activityRepository;

    public ProgressTracker(SkillRepository skillRepository, ActivityRepository activityRepository) {
        this.skillRepository = skillRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * Basic formula. Returns 0 when there are no skills (avoids division by zero).
     * The result is rounded to the nearest whole number.
     */
    public int calculatePercentage(int completed, int total) {
        if (total <= 0) {
            return 0;
        }
        return (int) Math.round(completed * 100.0 / total);
    }

    /**
     * Method overloading: same method name, but takes a list of skills.
     */
    public int calculatePercentage(List<Skill> skills) {
        return calculatePercentage(countByStatus(skills, SkillStatus.COMPLETED), skills.size());
    }

    /**
     * Method overloading again: progress of a full learning path.
     */
    public int calculatePercentage(LearningPath path) {
        return path == null ? 0 : calculatePercentage(path.getAllSkills());
    }

    public int countByStatus(List<Skill> skills, SkillStatus status) {
        int count = 0;
        for (Skill skill : skills) {
            if (skill.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns percentage completed for each level, e.g. {Beginner=100, Intermediate=40, Advanced=0}.
     */
    public Map<String, Integer> getLevelProgress(LearningPath path) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (path == null) {
            return result;
        }
        for (String level : path.getLevels()) {
            result.put(level, calculatePercentage(path.getSkillsForLevel(level)));
        }
        return result;
    }

    /**
     * Changes the status of one skill and saves it to progress.txt straight away.
     */
    public void updateSkillStatus(String email, Skill skill, SkillStatus newStatus)
            throws ValidationException, IOException {
        if (skill == null || newStatus == null) {
            throw new ValidationException("Please choose a valid skill and status.");
        }
        if (skill.getStatus() == newStatus) {
            throw new ValidationException("\"" + skill.getSkillName() + "\" is already " + newStatus.getLabel() + ".");
        }
        skillRepository.saveStatus(email, skill.getSkillId(), newStatus);
        skill.setStatus(newStatus);
        activityRepository.log(email, "Marked \"" + skill.getSkillName() + "\" as " + newStatus.getLabel());
    }
}
