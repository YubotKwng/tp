package seedu.javaninja;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class QuizManagerTest {

    private QuizManager quizManager;
    private final String RESULTS_FILE_PATH = "data/results.txt";

    @BeforeEach
    public void setUp() {
        // Clean up results file before each test
        File file = new File(RESULTS_FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        quizManager = new QuizManager();
    }

    @Test
    public void selectTopic_invalidTopicName_displaysError() {
        // Test selecting a topic that doesn't exist
        quizManager.selectTopic("InvalidTopicName");
        // No direct assertion, but expect no exception or crash
    }

    @Test
    public void addTopic_validTopic_addsSuccessfully() {
        quizManager.addTopic(new Topic("Java Basics"));

        assertEquals(2, quizManager.getTopicsCount());  // 1 default + 1 added
    }

    @Test
    public void removeTopic_existingTopic_removesSuccessfully() {
        Topic topic = new Topic("Java Basics");
        quizManager.addTopic(topic);

        // Remove the topic
        quizManager.removeTopic(topic);

        // Check if the topic was successfully removed
        assertEquals(1, quizManager.getTopicsCount());
    }

    @Test
    public void getPastResults_withResults_returnsCorrectResults() {
        // Adding a topic and simulating a quiz
        Topic topic = new Topic("Java Basics");
        quizManager.addTopic(topic);
        quizManager.startQuiz(topic);

        // Check if the past results contain the quiz score and comment
        String pastResults = quizManager.getPastResults();
        String expectedResult = "Score: 0%, Comment: Better luck next time!\n";

        assertEquals(expectedResult, pastResults);
    }

    @Test
    public void saveResults_savesToFileCorrectly() throws IOException {
        // Add a topic, complete a quiz and check if the results are saved
        Topic topic = new Topic("Java Basics");
        quizManager.addTopic(topic);
        quizManager.startQuiz(topic);  // Simulates completing a quiz

        // Load saved results from file and check content
        String savedResults = Files.readString(Path.of(RESULTS_FILE_PATH));
        String expectedSavedResults = "Score: 0%, Comment: Better luck next time!\n";

        assertEquals(expectedSavedResults, savedResults);
    }

    @Test
    public void loadResultsFromFile_correctlyLoadsResults() throws IOException {
        // Simulate a previous result saved in the file
        String previousResult = "Score: 80%, Comment: Good job!\n";
        Files.writeString(Path.of(RESULTS_FILE_PATH), previousResult);

        // Reload the QuizManager to simulate restarting the program
        quizManager = new QuizManager();

        // Check if the loaded result matches the previous result
        String loadedResults = quizManager.getPastResults();
        assertEquals(previousResult, loadedResults);
    }
}
