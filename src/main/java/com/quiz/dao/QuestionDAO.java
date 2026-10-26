package com.quiz.dao;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.quiz.model.Question;

/** Reads questions from questions.xml and lets the admin add new ones. */
public class QuestionDAO {

    private static final String FILE = "questions.xml";

    /** All questions in the file (used by the admin dashboard). */
    public List<Question> getAll() {
        synchronized (XmlHelper.LOCK) {
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));
            NodeList list = doc.getElementsByTagName("question");
            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < list.getLength(); i++) {
                questions.add(toQuestion((Element) list.item(i)));
            }
            return questions;
        }
    }

    /** Only the questions of one level, e.g. "Easy". */
    public List<Question> getByLevel(String level) {
        List<Question> result = new ArrayList<>();
        for (Question q : getAll()) {
            if (q.getLevel().equals(level)) {
                result.add(q);
            }
        }
        return result;
    }

    /** Appends a new question to questions.xml. The id is (highest id + 1). */
    public void add(String level, String text, String[] options, int correctIndex) {
        synchronized (XmlHelper.LOCK) {
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));

            int maxId = 0;
            NodeList list = doc.getElementsByTagName("question");
            for (int i = 0; i < list.getLength(); i++) {
                int id = Integer.parseInt(((Element) list.item(i)).getAttribute("id"));
                maxId = Math.max(maxId, id);
            }

            Element q = doc.createElement("question");
            q.setAttribute("id", String.valueOf(maxId + 1));
            q.setAttribute("level", level);
            XmlHelper.addChild(doc, q, "text", text);
            for (String option : options) {
                XmlHelper.addChild(doc, q, "option", option);
            }
            XmlHelper.addChild(doc, q, "correct", String.valueOf(correctIndex));

            doc.getDocumentElement().appendChild(q);
            XmlHelper.save(doc, XmlHelper.getFile(FILE));
        }
    }

    /** Converts one <question> XML element into a Question object. */
    private Question toQuestion(Element e) {
        NodeList optionNodes = e.getElementsByTagName("option");
        String[] options = new String[optionNodes.getLength()];
        for (int i = 0; i < options.length; i++) {
            options[i] = optionNodes.item(i).getTextContent();
        }
        return new Question(
                Integer.parseInt(e.getAttribute("id")),
                e.getAttribute("level"),
                XmlHelper.childText(e, "text"),
                options,
                Integer.parseInt(XmlHelper.childText(e, "correct")));
    }
}
