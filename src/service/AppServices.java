package service;

import repository.ActivityRepository;
import repository.FileManager;
import repository.GoalRepository;
import repository.SkillRepository;
import repository.StudentRepository;

/**
 * Creates every repository and service once and keeps them together,
 * so all screens of the application share the same objects (and the same logged-in student).
 */
public class AppServices {

    private final AuthenticationService authenticationService;
    private final CareerService careerService;
    private final RecommendationService recommendationService;
    private final ProgressTracker progressTracker;
    private final GoalManager goalManager;
    private final ActivityRepository activityRepository;

    /**
     * @param dataFolder folder where the text files are stored, normally "data"
     */
    public AppServices(String dataFolder) {
        FileManager fileManager = new FileManager(dataFolder);
        StudentRepository studentRepository = new StudentRepository(fileManager);
        SkillRepository skillRepository = new SkillRepository(fileManager);
        GoalRepository goalRepository = new GoalRepository(fileManager);
        activityRepository = new ActivityRepository(fileManager);

        authenticationService = new AuthenticationService(studentRepository, skillRepository,
                goalRepository, activityRepository);
        careerService = new CareerService(studentRepository, activityRepository);
        recommendationService = new RecommendationService(careerService, skillRepository);
        progressTracker = new ProgressTracker(skillRepository, activityRepository);
        goalManager = new GoalManager(goalRepository, activityRepository);
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public CareerService getCareerService() {
        return careerService;
    }

    public RecommendationService getRecommendationService() {
        return recommendationService;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public GoalManager getGoalManager() {
        return goalManager;
    }

    public ActivityRepository getActivityRepository() {
        return activityRepository;
    }
}
