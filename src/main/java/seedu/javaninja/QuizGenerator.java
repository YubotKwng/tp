package seedu.javaninja;

import seedu.javaninja.question.Question;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

public class QuizGenerator {
    private final Cli cli;
    private final TopicManager topicManager;
    private int questionLimit;
    private int timeLimitInSeconds;
    private Topic topic;
    private Quiz currentQuiz;


    public QuizGenerator(TopicManager topicManager, Cli cli) {
        this.topicManager = topicManager;
        this.cli = cli;
        this.questionLimit = 0;
        this.timeLimitInSeconds = 0;
        this.topic = null;
        this.currentQuiz = null;
    }

    /**
     * Starts a timed quiz on a specific topic.
     * @param topicName The name of the topic.
     */
    public boolean selectTimedQuiz(String topicName) {
        topic = topicManager.getTopic(topicName);
        if (topic == null) {
            cli.printMessage("Topic not found: " + topicName);
            return false;
        }

        currentQuiz = new Quiz(topic, cli);

        questionLimit = getQuestionLimitFromUser(topic);
        timeLimitInSeconds = getTimeLimitInSecondsFromUser();


        currentQuiz.start(timeLimitInSeconds, questionLimit);
        return true;
    }

    /**
     * Starts an untimed quiz on a specific topic.
     * @param topicName The name of the topic.
     */
    public boolean selectUntimedQuiz(String topicName) {
        topic = topicManager.getTopic(topicName);
        if (topic == null) {
            cli.printMessage("Topic not found: " + topicName);
            return false;
        }

        timeLimitInSeconds = -1;

        questionLimit = getQuestionLimitFromUser(topic);
        currentQuiz = new Quiz(topic, cli);

        currentQuiz.start(timeLimitInSeconds, questionLimit);// -1 for no time limit
        return true;
    }

    /**
     * Starts a quiz with random topics, either timed or untimed.
     * @param isTimed Determines if the quiz should be timed.
     */
    public boolean selectRandomTopicsQuiz(boolean isTimed) {
        int numTopics = getNumTopicsFromUser();
        int numQuestions = getNumQuestionsFromUser();
        questionLimit = numQuestions * numTopics;

        topic = generateRandomQuiz(numTopics, numQuestions);
        currentQuiz = new Quiz(topic, cli);

        if (isTimed) {
            timeLimitInSeconds = getTimeLimitInSecondsFromUser();
            currentQuiz.start(timeLimitInSeconds, questionLimit);
        } else {
            timeLimitInSeconds = -1;
            currentQuiz.start(timeLimitInSeconds, questionLimit); // -1 for no time limit
        }
        return true;
    }

    /**
     * Generates a random quiz topic with a specified number of topics and questions.
     * @param numTopics Number of topics to include.
     * @param numQuestions Number of questions per topic.
     * @return A Topic containing randomly selected questions.
     */
    private Topic generateRandomQuiz(int numTopics, int numQuestions) {
        List<Topic> allTopics = topicManager.getTopics();

        // Ensure that we do not attempt to select more topics than available
        if (numTopics > allTopics.size()) {
            numTopics = allTopics.size();
        }

        Collections.shuffle(allTopics);
        List<Topic> selectedTopics = allTopics.stream()
            .limit(numTopics)
            .toList();

        List<Question> randomQuestions = selectedTopics.stream()
            .flatMap(topic -> topic.getQuestions().stream())
            .collect(Collectors.toList());

        Collections.shuffle(randomQuestions);
        List<Question> limitedQuestions = randomQuestions.stream()
            .limit(numQuestions)
            .toList();

        Topic randomQuiz = new Topic("Random Quiz");
        for (Question question : limitedQuestions) {
            randomQuiz.addQuestion(question);
        }

        return randomQuiz;
    }

    /**
     * Prompts the user for a question limit, ensuring it's within the topic's question size.
     * @param topic The topic to quiz on.
     * @return Number of questions to use for the quiz.
     */
    private int getQuestionLimitFromUser(Topic topic) {
        int maxQuestions = topic.getQuestions().size();
        final int UPPER_LIMIT = 1000; // Define a practical upper limit for question count

        while (true) {
            cli.printMessage("Enter the number of questions you want to attempt (Max " + maxQuestions + "): ");
            try {
                int questionLimit = Integer.parseInt(cli.readInput().trim());

                if (questionLimit > 0 && questionLimit <= maxQuestions) {
                    return questionLimit;
                } else if (questionLimit > UPPER_LIMIT) {
                    cli.printMessage("The number entered is too large. Please enter a realistic number below " + UPPER_LIMIT + ".");
                } else {
                    cli.printMessage("Invalid number. Please enter a number between 1 and " + maxQuestions + ".");
                }
            } catch (NumberFormatException e) {
                cli.printMessage("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Prompts the user for a time limit for timed quizzes.
     * @return Time limit in seconds.
     */
    public int getTimeLimitInSecondsFromUser() {
        while (true) {
            cli.printMessage("Enter the number of minutes for the quiz (or 0 for seconds): ");
            try {
                int minutes = Integer.parseInt(cli.readInput().trim());
                if (minutes >= 0) {
                    if (minutes == 0) {
                        cli.printMessage("Enter the number of seconds: ");
                        int seconds = Integer.parseInt(cli.readInput().trim());
                        if (seconds > 0) {
                            return seconds;
                        }
                    } else {
                        return minutes * 60;
                    }
                }
                cli.printMessage("Invalid input. Time must be a positive number.");
            } catch (NumberFormatException e) {
                cli.printMessage("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Gets the number of topics to include for a random quiz.
     * @return Number of topics.
     */
    private int getNumTopicsFromUser() {
        while (true) {
            cli.printMessage("Enter the number of topics to include in the random quiz: ");
            try {
                int numTopics = Integer.parseInt(cli.readInput().trim());
                if (numTopics > 0) {
                    return numTopics;
                }
                cli.printMessage("Invalid input. Number of topics must be greater than zero.");
            } catch (NumberFormatException e) {
                cli.printMessage("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Gets the number of questions per topic for a random quiz.
     * @return Number of questions per topic.
     */
    private int getNumQuestionsFromUser() {
        while (true) {
            cli.printMessage("Enter the number of questions per topic for the random quiz: ");
            try {
                int numQuestions = Integer.parseInt(cli.readInput().trim());
                if (numQuestions > 0) {
                    return numQuestions;
                }
                cli.printMessage("Invalid input. Number of questions must be greater than zero.");
            } catch (NumberFormatException e) {
                cli.printMessage("Invalid input. Please enter a valid number.");
            }
        }
    }


    public int getTimeLimitInSeconds() {
        return timeLimitInSeconds;
    }

    public int getQuizScore() {
        return currentQuiz.getScore();
    }

    public String getTopicName() {
        return topic.getName();
    }

    public int getQuestionLimit() {
        return questionLimit;
    }
}
