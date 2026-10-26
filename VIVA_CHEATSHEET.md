# Viva Cheat Sheet (one page)

**What it is:** Online quiz web app. **Java Servlets + JSP + XML (DOM)** only. No database, no framework. Runs on Tomcat (Tomcat 9, javax.servlet). Pattern: **MVC**.

## The 10-second story
Browser > `AuthFilter` (login check) > **Servlet** (controller) > **DAO** (reads/writes XML with DOM) > data goes to the request > **JSP** (view) builds HTML > Browser. State between requests is kept in **HttpSession**.

## URL > Servlet > JSP map
| URL | Servlet | JSP | What it does |
|---|---|---|---|
| `/login`, `/adminlogin` | LoginServlet | login / admin-login | check hash, create session |
| `/register` | RegisterServlet | register | validate, save to users.xml |
| `/levels` | LevelServlet | select-level | choose Easy / Medium / Hard |
| `/start` (POST) | StartQuizServlet | none (redirect) | pick 10 random questions, fill session |
| `/question` | QuestionServlet | question | show current question + time left |
| `/answer` (POST) | AnswerServlet | none (redirect) | score, next question or finish and save |
| `/result` | ResultServlet | result | score, %, right/wrong review |
| `/myresults` | MyResultsServlet | my-results | past attempts from results.xml |
| `/admin/dashboard`, `/admin/addquestion` | AdminDashboard / AddQuestion | admin-dashboard / add-question | admin panel, add question |

## Key terms (one line each)
- **GET** = ask for a page (no changes). **POST** = send data / change something (form body, not in URL).
- **forward** = server-side jump, same request, URL stays. **redirect** = browser makes a new request, URL changes.
- **Post/Redirect/Get** = after every POST we redirect, so Refresh cannot resubmit a form.
- **HttpSession** = per-user memory on the server, found by the `JSESSIONID` cookie.
- **DOM** = whole XML file loaded as a tree in memory (read, change, save). SAX = event-based, read-only.
- **DAO** = class that hides how data is stored (`UserDAO`, `QuestionDAO`, `ResultDAO`). **Model** = plain data classes (`User`, `Question`, `Result`).
- **Filter** = code that runs before servlets (`AuthFilter`). **Listener** = runs at startup (`AppInitListener`).
- **JSTL / EL** = `<c:forEach>`, `<c:out>`, `${name}` in JSPs instead of Java code. `<c:out>` escapes HTML (stops XSS).
- **web.xml** = deployment descriptor (welcome page, error pages, data folder). **WAR** = zipped web app for `webapps`.
- **SHA-256 hash** = one-way fingerprint of the password; we never store the real password.

## XML structures
```
questions.xml  <question id level> <text/> <option/>x4 <correct>0-3</correct> </question>
users.xml      <user> <username/> <password>hash</password> <role/> <fullName/> </user>
results.xml    <result> <username/> <level/> <score/> <total/> <date/> </result>
```
Folder: `OnlineQuizData` in the user's home (outside the project, so redeploy keeps data).

## Session attributes
`user` | `quizLevel` `quizQuestions` `quizIndex` `quizScore` `quizChosen` `quizStartedAt` `quizFinished`

## Edge case > how we handle it
| Case | Answer |
|---|---|
| Not logged in, typed a URL | `AuthFilter` redirects to login |
| Student opens `/admin/...` | filter checks role, redirects to admin login |
| Opened a `.jsp` directly | 404, JSPs are in `WEB-INF` |
| No level chosen | `/start` validates, back to `/levels` with a message |
| Refresh question page | read-only GET, server keeps start time, timer continues |
| Refresh result page | result already saved once; page only reads the session |
| Back button / double click | hidden `qIndex` must match `quizIndex`, else ignored |
| JavaScript off / slow answer | server rejects answers after 10 s + 2 s grace |
| Back after logout | pages sent with no-cache headers + filter |
| Duplicate username / bad input | server-side validation, message, form refilled |

## Numbers and logins
10 s per question | 10 questions per quiz | 12 questions per level (36 total) | 3 levels | session timeout 30 min
Logins: `admin / admin123`, `student1 / student123`

## Swing > Web (if they ask about the old version)
`Timer` > JavaScript countdown + server time check | `index, score` fields > session attributes | `JProgressBar` > HTML bar | `JOptionPane` result > `result.jsp` | `restartGame()` > `/levels` + new `/start`

## If they ask "what would you improve?"
Salted hashing (BCrypt), a real database, CSRF tokens, edit/delete questions, pagination, quiz time stored per attempt.

## Who answers what
**M1:** login, register, admin, filter, JSP forms. **M2:** quiz flow, session, timer, scoring. **M3:** XML/DOM, DAO, models, results, deployment.
