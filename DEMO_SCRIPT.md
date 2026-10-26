# Viva Demo Script (about 7 minutes, 3 speakers)

**Before you start (5 min earlier):** delete the `OnlineQuizData` folder in your user home (fresh data), start Tomcat, and open `http://localhost:8080/OnlineQuiz/` in Chrome. Open that `OnlineQuizData` folder in a second window with `users.xml`, `questions.xml`, `results.xml` ready to open. Have the project open in your IDE. Use two browser profiles or one normal and one incognito window (student and admin).

| Time | Who | Do | Say |
|---|---|---|---|
| 0:00 | M1 | Show the landing page. | "Online Quiz built with Servlets, JSP and XML only: no database, no framework. We follow MVC: servlets are controllers, JSPs are views, DAO classes plus model classes are the model." |
| 0:30 | M1 | **Register** a new student with a short password to show an error, then register correctly. Try the same username again. | "Validation is on the server, not only in the browser. Passwords are saved as SHA-256 hashes." |
| 1:15 | M3 | Open `users.xml`. | "Registration was written with DOM: load the document, create an element, append it, save. It is in the `OnlineQuizData` folder so redeploying never deletes data." |
| 1:45 | M1 | **Log in** as the new student. | "Login creates an `HttpSession` and stores the `User` in it." |
| 2:00 | M2 | Click **Start Easy**. Let the first question time out without answering. Answer the next one, then **refresh the page** (F5). | "The timer is JavaScript, but the server stores when the question started. Refresh does not reset the time, and a late answer is rejected on the server." |
| 3:00 | M2 | Answer quickly to the end. Point at the progress bar on the way. | "Quiz state lives in the session: questions, index, score, chosen answers. Every answer is a POST followed by a redirect." |
| 4:00 | M3 | On the result page, show the percentage and the green/red review. Click **My Results**. Open `results.xml`. | "Result is saved once, after the last answer, so refreshing the result page does not duplicate it. My Results reads the user's rows from results.xml." |
| 4:45 | M2 | **Edge cases:** press the browser **Back** button on the result page and on a question. Type `/OnlineQuiz/question` and `/OnlineQuiz/start` into the address bar. | "A hidden question number detects stale pages. Direct URLs without a quiz send you back to level selection with a message." |
| 5:30 | M1 | **Logout**, then type `/OnlineQuiz/levels` and `/OnlineQuiz/admin/dashboard` directly. | "`AuthFilter` checks every protected URL before any servlet runs. JSPs are under WEB-INF, so they cannot be opened directly." |
| 6:00 | M1 | **Admin login** (`admin` / `admin123`), open **Add Question**, add a question, then show the dashboard list. | "Admin panel is protected by role. The question is validated, then appended to questions.xml." |
| 6:40 | M3 | Open `questions.xml`, scroll to the new question. | "The new question joins its level's pool, so a later quiz can pick it." |
| 7:00 | All | Stop. | "Questions?" |

## Plan B if something fails
- **Tomcat will not start:** another program uses port 8080. Stop it or change the port; keep the WAR file (`mvn package`) ready for standalone Tomcat.
- **A page shows an error:** say "it shows our error page, not a stack trace, by design", go back to Home and continue.
- **Time is short:** skip the incognito admin part and the edge cases; keep register, quiz, result and the XML files.

## Order of speaking
M1 opens and closes the demo (accounts, security, admin), M2 runs the quiz and timer, M3 shows the XML each time data changes. If the examiner asks about any step, the person who owns that area (see `VIVA_GUIDE.md`) answers.
