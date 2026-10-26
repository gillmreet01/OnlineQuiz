# Viva Guide: Online Quiz Application

## 1. Team split

| | Owns (files) | Must be able to explain |
|---|---|---|
| **Member 1** | `LoginServlet`, `RegisterServlet`, `LogoutServlet`, `AdminDashboardServlet`, `AddQuestionServlet`, `AuthFilter`; JSPs: `index`, `login`, `admin-login`, `register`, `admin-dashboard`, `add-question`, `error`, `header/footer.jspf`; `style.css` | Forms, validation, login/session creation, roles, admin panel, JSP + JSTL + EL |
| **Member 2** | `LevelServlet`, `StartQuizServlet`, `QuestionServlet`, `AnswerServlet`; JSPs `select-level`, `question`; `timer.js`; `AppConfig` | Quiz flow, session attributes, timer (client + server), scoring, refresh/back handling |
| **Member 3** | `model/*`, `dao/*` (DOM), `AppInitListener`, `ResultServlet`, `MyResultsServlet`, JSPs `result`, `my-results`; `web.xml`, XML files, deployment | DOM read/write, XML structure, DAO pattern, results, Tomcat setup / WAR |

## 2. Full request flow (everyone must know this)

**MVC:** Model = `model/` + `dao/` (data). View = JSPs in `WEB-INF/views`. Controller = servlets. Servlets never print HTML; JSPs never read XML.

```
Browser ──URL──> [AuthFilter] ──> Servlet (controller) ──> DAO ──> XML file (DOM)
                                      │  ◄── objects ──────┘
                                      │ request.setAttribute(...)
                                      ▼
                              forward to JSP (view) ──HTML──> Browser
```

**GET (show a page), e.g. "My Results":** Click link `/myresults` -> `AuthFilter` checks the session has a user -> `MyResultsServlet.doGet` reads the username from the session -> `ResultDAO.getByUsername` parses `results.xml` with DOM and returns `List<Result>` -> servlet puts it in the request (`setAttribute("results", ...)`) -> `forward` to `my-results.jsp` -> JSP loops with `<c:forEach>` and sends HTML.

**POST (submit a form), e.g. Login:** `login.jsp` form `POST /login` -> `LoginServlet.doPost` reads parameters -> `UserDAO.authenticate` (DOM read of `users.xml`, compare SHA-256 hashes) -> wrong: forward back to `login.jsp` with an error; right: new session, `session.setAttribute("user", user)` -> `sendRedirect("/levels")` (Post/Redirect/Get, so Refresh does not resubmit).

**Whole quiz journey:**
1. `/levels` (`LevelServlet` -> `select-level.jsp`) - student clicks "Start Medium" (POST form with hidden `level`).
2. `/start` (`StartQuizServlet`) - validates level, `QuestionDAO.getByLevel` (reads `questions.xml`), shuffles, takes 10, stores `quizQuestions, quizIndex=0, quizScore=0, quizChosen, quizStartedAt` in the **session**, redirects to `/question`.
3. `/question` (`QuestionServlet`, GET) - reads current question from session, computes remaining milliseconds, forwards to `question.jsp`. `timer.js` counts down.
4. Student clicks Next **or** timer hits 0 -> the form auto-submits `POST /answer` with `qIndex` + chosen `answer` (none = timeout).
5. `AnswerServlet` - checks `qIndex` matches, checks time limit, compares with `correctIndex`, updates score, `quizIndex++`, **redirects** to `/question` (next question) or, after the last one, saves via `ResultDAO.save` (appends to `results.xml`), sets `quizFinished`, redirects to `/result`.
6. `/result` (`ResultServlet`) - reads score/questions/chosen from the session, forwards to `result.jsp` (score, percentage, right/wrong review, Restart button -> `/levels`).
7. `/myresults` - lists all attempts of this user from `results.xml`.

**Session = the quiz's memory.** HTTP is stateless; Tomcat gives each browser a `JSESSIONID` cookie and keeps a server-side `HttpSession` map. We keep `user` and all `quiz*` values there.

---

## 3. Member 1 - 10 viva questions (login, register, admin, JSP, forms)

1. **Why is there a filter?** One place (`AuthFilter`) checks login/role for all protected URLs instead of repeating the check in every servlet. It runs before the servlet.
2. **How does login work?** `doPost` reads username/password, `UserDAO.authenticate` compares the SHA-256 hash with users.xml; on success we store the `User` in the session and redirect.
3. **Why hash passwords?** So the real password is not readable in `users.xml`; a hash cannot easily be reversed. (Real systems also add a salt, e.g. BCrypt.)
4. **What is `session.invalidate()` before creating a new session at login?** It destroys any old session so a new session ID is issued (prevents session fixation).
5. **`forward` vs `sendRedirect`?** Forward: server-side, same request, URL unchanged, request attributes kept. Redirect: browser makes a new request, URL changes. We redirect after successful POSTs.
6. **Why validate on the server when HTML has `required`?** Browser checks can be bypassed (curl, dev tools). Server validation is the real protection.
7. **How is the admin login separate?** Own URL `/adminlogin` and JSP; `LoginServlet` accepts only admins there and only students on `/login`. `/admin/*` is protected by the filter and checks `user.isAdmin()`.
8. **Why are JSPs inside `WEB-INF/views`?** Files in `WEB-INF` cannot be opened by URL; users must go through a servlet (and the filter). That is why `/login.jsp` gives 404.
9. **What is `<c:out>` / XSS?** `<c:out>` escapes `<`, `>`, `&`. If a user registers with `<script>` in their name, it is shown as text, not executed.
10. **How does the admin add a question and what stops bad data?** `AddQuestionServlet` checks level, text, 4 options and correct index, then `QuestionDAO.add` appends to `questions.xml`. Invalid input forwards back to the form with an error and refilled fields (`${param.x}`).

## 4. Member 2 - 10 viva questions (quiz flow, session, timer, scoring)

1. **Why session and not static variables?** Static/servlet fields are shared by all users. The session is per user, so two students can take quizzes at once.
2. **List the session attributes.** `user, quizLevel, quizQuestions, quizIndex, quizScore, quizChosen, quizStartedAt, quizFinished`.
3. **How does the 10-second timer work?** `timer.js` counts down and auto-submits the form at 0. Server also stores `quizStartedAt` and rejects answers later than 10 s + 2 s grace, so disabling JavaScript does not help.
4. **What happens if the user refreshes the question page?** `QuestionServlet` is read-only (GET). It shows the same question and gives the *remaining* time, so refresh does not reset the timer.
5. **Why is `/answer` a POST and why does it redirect?** It changes state (score, index), so it must be POST. Redirect after it means pressing Refresh repeats a harmless GET, not the POST. (PRG pattern)
6. **What is `qIndex` hidden field for?** Back button/double click can resend an old form. If `qIndex` is not the current index, the answer is ignored.
7. **How is scoring done?** `picked == question.getCorrectIndex()` -> `quizScore++`. Percentage = `score * 100.0 / total`, same formula as the Swing version.
8. **What if no level is chosen / user types `/question` directly?** `StartQuizServlet` validates the level; `QuestionServlet`/`AnswerServlet`/`ResultServlet` see `quizQuestions == null` and redirect to `/levels?msg=nolevel`.
9. **How do you pick questions?** `getByLevel` returns the pool; `Collections.shuffle`, then `subList(0, 10)` so attempts differ.
10. **How is the Swing logic mapped?** Swing `Timer`+`timeLeft` -> JavaScript countdown; `index/score` fields -> session attributes; `JProgressBar` -> HTML bar with `width:${progress}%`; `restartGame()` -> `/levels` + new `/start`; `JOptionPane` result -> `result.jsp`.

## 5. Member 3 - 10 viva questions (XML/DOM, models, results, deployment)

1. **What is DOM parsing?** The whole XML file is loaded into memory as a tree of nodes; we navigate it with `getElementsByTagName` and can modify then save it.
2. **DOM vs SAX?** DOM: tree in memory, easy read/write, good for small files. SAX: event-based, low memory, read-only. Our files are small and we must write, so DOM.
3. **How do you write a new record?** Load Document, `createElement`, `setTextContent`, `appendChild` to the root, then `Transformer` writes it back (`XmlHelper.save`).
4. **Why `synchronized (XmlHelper.LOCK)`?** Two requests could read the file, both add a record, and the second save would erase the first. The lock makes read-modify-write one step at a time.
5. **Why write to a `.tmp` file then move it?** If the server stops mid-write, the real file stays intact instead of half-written.
6. **What is a DAO / why model classes?** DAO hides the storage: servlets call `userDAO.findByUsername()` and do not know it is XML. Models (`User`, `Question`, `Result`) are simple objects passed between layers. We could move to a database by changing only the DAOs.
7. **Show the XML structure.** `questions.xml`: `<question id level><text/><option/>x4<correct/></question>`; `users.xml`: `<user><username/><password/><role/><fullName/></user>`; `results.xml`: `<result><username/><level/><score/><total/><date/></result>`.
8. **Why is the data folder outside the project?** Redeploying replaces the project folder and would delete new users/results. `AppInitListener` creates `~/OnlineQuizData` from `WEB-INF/seed` on first start.
9. **When is a result saved, and why not twice?** In `AnswerServlet` after the last answer, once; `ResultServlet` only reads from the session, so refreshing the result page cannot create duplicates.
10. **Explain deployment and Tomcat 9 vs 10.** WAR = zipped web app copied to `webapps`; `web.xml` is the deployment descriptor. Tomcat 10+ uses `jakarta.servlet.*`, Tomcat 9 uses `javax.servlet.*` (Oracle handed Java EE to Eclipse, which renamed the packages).

## 6. Bonus questions anyone may get
- *Where are passwords/answers stored?* XML in the data folder, passwords hashed.
- *What stops a student opening admin pages?* The filter plus the role check.
- *What would you improve?* Salted hashing (BCrypt), a real database, CSRF tokens, pagination, edit/delete questions.
