package com.quiz.util;

/**
 * Constants used across the whole application.
 * Keeping them in one place means we change a value only once.
 */
public class AppConfig {

    /** Time allowed for each question (seconds). */
    public static final int SECONDS_PER_QUESTION = 10;

    /** How many questions one quiz contains. */
    public static final int QUESTIONS_PER_QUIZ = 10;

    /** Extra seconds we allow for network delay before treating an answer as "too late". */
    public static final int GRACE_SECONDS = 2;

    /** The three difficulty levels (must match the level="" attribute in questions.xml). */
    public static final String[] LEVELS = {"Easy", "Medium", "Hard"};

    /** Returns true only if the text is exactly one of our levels. */
    public static boolean isValidLevel(String level) {
        for (String l : LEVELS) {
            if (l.equals(level)) {
                return true;
            }
        }
        return false;
    }
}
