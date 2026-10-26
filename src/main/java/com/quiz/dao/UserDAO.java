package com.quiz.dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.quiz.model.User;

/**
 * DAO = Data Access Object. This class is the ONLY place that knows how users are stored.
 * Servlets just call findByUsername(), authenticate() and register().
 */
public class UserDAO {

    private static final String FILE = "users.xml";

    /** Turns a password into a SHA-256 hash (64 hex characters). */
    public static String hash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new DataException("SHA-256 not available", e);
        }
    }

    /** Finds a user by username (ignoring upper/lower case). Returns null if not found. */
    public User findByUsername(String username) {
        synchronized (XmlHelper.LOCK) {
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));
            NodeList list = doc.getElementsByTagName("user");
            for (int i = 0; i < list.getLength(); i++) {
                Element e = (Element) list.item(i);
                if (XmlHelper.childText(e, "username").equalsIgnoreCase(username)) {
                    return new User(
                            XmlHelper.childText(e, "username"),
                            XmlHelper.childText(e, "password"),
                            XmlHelper.childText(e, "role"),
                            XmlHelper.childText(e, "fullName"));
                }
            }
            return null;
        }
    }

    /** Returns the user if username + password are correct, otherwise null. */
    public User authenticate(String username, String password) {
        User user = findByUsername(username);
        if (user != null && user.getPasswordHash().equals(hash(password))) {
            return user;
        }
        return null;
    }

    /** Adds a new student. Returns false (and saves nothing) if the username is already taken. */
    public boolean registerStudent(String username, String password, String fullName) {
        synchronized (XmlHelper.LOCK) {
            if (findByUsername(username) != null) {
                return false;
            }
            Document doc = XmlHelper.load(XmlHelper.getFile(FILE));
            Element user = doc.createElement("user");
            XmlHelper.addChild(doc, user, "username", username);
            XmlHelper.addChild(doc, user, "password", hash(password));
            XmlHelper.addChild(doc, user, "role", "student");
            XmlHelper.addChild(doc, user, "fullName", fullName);
            doc.getDocumentElement().appendChild(user);
            XmlHelper.save(doc, XmlHelper.getFile(FILE));
            return true;
        }
    }
}
