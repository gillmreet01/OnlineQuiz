package com.quiz.model;

/** One multiple-choice question. Matches one <question> element in questions.xml. */
public class Question {

    private final int id;
    private final String level;        // Easy / Medium / Hard
    private final String text;
    private final String[] options;    // always 4 options
    private final int correctIndex;    // 0 = first option ... 3 = fourth option

    public Question(int id, String level, String text, String[] options, int correctIndex) {
        this.id = id;
        this.level = level;
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public int getId()             { return id; }
    public String getLevel()       { return level; }
    public String getText()        { return text; }
    public String[] getOptions()   { return options; }
    public int getCorrectIndex()   { return correctIndex; }
}
