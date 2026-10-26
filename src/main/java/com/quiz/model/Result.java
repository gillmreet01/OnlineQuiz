package com.quiz.model;

/** One finished quiz attempt. Matches one <result> element in results.xml. */
public class Result {

    private final String username;
    private final String level;
    private final int score;
    private final int total;
    private final String date; // already formatted, e.g. "2026-10-26 14:30"

    public Result(String username, String level, int score, int total, String date) {
        this.username = username;
        this.level = level;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    public String getUsername() { return username; }
    public String getLevel()    { return level; }
    public int getScore()       { return score; }
    public int getTotal()       { return total; }
    public String getDate()     { return date; }

    /** Percentage is calculated, not stored. Same formula as the old Swing app. */
    public int getPercentage() {
        if (total == 0) {
            return 0;
        }
        return (int) (score * 100.0 / total);
    }
}
