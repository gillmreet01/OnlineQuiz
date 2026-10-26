package com.quiz.model;

/** A registered person (student or admin). Matches one <user> element in users.xml. */
public class User {

    private final String username;
    private final String passwordHash; // we never store the plain password
    private final String role;         // "student" or "admin"
    private final String fullName;

    public User(String username, String passwordHash, String role, String fullName) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.fullName = fullName;
    }

    public String getUsername()     { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole()         { return role; }
    public String getFullName()     { return fullName; }

    /** Used in JSP as ${sessionScope.user.admin}. */
    public boolean isAdmin() {
        return "admin".equals(role);
    }
}
