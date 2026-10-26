package com.quiz.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.quiz.model.Result;

/** Saves finished quiz attempts to results.xml and reads them back. */
public class ResultDAO {

    private static final String FILE = "results.xml";

    /** Appends one attempt to results.xml (the date is filled in automatically). */
    public void save(String username, String level, int score, int total) {
        synchronized (XmlHelper.LOCK) {
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));
            Element r = doc.createElement("result");
            XmlHelper.addChild(doc, r, "username", username);
            XmlHelper.addChild(doc, r, "level", level);
            XmlHelper.addChild(doc, r, "score", String.valueOf(score));
            XmlHelper.addChild(doc, r, "total", String.valueOf(total));
            XmlHelper.addChild(doc, r, "date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            doc.getDocumentElement().appendChild(r);
            XmlHelper.save(doc, XmlHelper.getFile(FILE));
        }
    }

    /** All attempts of one user, newest first. */
    public List<Result> getByUsername(String username) {
        synchronized (XmlHelper.LOCK) {
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));
            NodeList list = doc.getElementsByTagName("result");
            List<Result> results = new ArrayList<>();
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                if (XmlHelper.childText(e, "username").equalsIgnoreCase(username)) {
                    results.add(new Result(
                            XmlHelper.childText(e, "username"),
                            XmlHelper.childText(e, "level"),
                            Integer.parseInt(XmlHelper.childText(e, "score")),
                            Integer.parseInt(XmlHelper.childText(e, "total")),
                            XmlHelper.childText(e, "date")));
                }
            }
            Collections.reverse(results); // the file is oldest-first, we want newest-first
            return results;
        }
    }
}
