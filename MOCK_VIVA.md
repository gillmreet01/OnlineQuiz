# Mock Viva Practice Sheet

Practise out loud, in this order, before the real viva. 25 questions, 12 with an examiner follow-up. Allow about 25 minutes for a full run.

## How to run a mock viva
1. Three people play **examiner**, **student A** and **student B**, and rotate after each round so everybody gets asked about every part.
2. The examiner reads only the question (and the follow-up, when it exists). The model answers below stay hidden until the answer has been given.
3. The student answers **out loud in 30-45 seconds**, ideally with the project open and pointing at the file or page.
4. The examiner scores the answer: **2** = correct and clear without hints, **1** = right idea but needed a hint or missed a detail, **0** = could not answer.
5. Anything scoring 0 or 1 goes on the "revise before the viva" list. Run it again until every question scores 2.

**Rule for real viva:** if you do not know, say what you do know and name the file where the answer lives ("it is handled in AnswerServlet, the qIndex check"). Never invent.

## Rounds
| Round | Questions | Who answers |
|---|---|---|
| 1. Warm-up | 1-2 | everyone, in turn |
| 2. Member 1 area | 3-8 | Member 1 first, others may add |
| 3. Member 2 area | 9-16 | Member 2 first |
| 4. Member 3 area | 17-24 | Member 3 first |
| 5. Cross-questions | 25 and every follow-up | an area owner is asked about someone else's part |

Round 5 matters: examiners often ask a member about a part they did not write.

## Questions and model answers

### 1. Explain your project in one minute.
*Asked to: Whole team*

**Model answer:** It is an online quiz app built only with Java Servlets, JSP and XML. Students register and log in, pick Easy, Medium or Hard, and answer 10 questions with a 10-second timer each. They see a score, a percentage and a right/wrong review, and they can open past attempts. An admin can add questions. All data is in three XML files read with DOM. The structure is MVC: servlets are controllers, JSPs are views, and DAO plus model classes are the model.

**Examiner follow-up:** Why no database or Spring?

**Answer:** The course rules require Servlets, JSP and XML, and XML lets us show DOM parsing. Only the DAO classes touch the files, so moving to a database later would change only the DAOs.

### 2. Show MVC in your project with one class for each part.
*Asked to: Whole team*

**Model answer:** Model: Question.java, User.java, Result.java and the DAOs. View: question.jsp. Controller: AnswerServlet. Servlets never print HTML and JSPs never read XML.

**Examiner follow-up:** Where would you change the code to show 5 questions per quiz instead of 10?

**Answer:** In AppConfig, the constant QUESTIONS_PER_QUIZ. StartQuizServlet reads it when it picks the questions.

### 3. What happens when I click Login?
*Asked to: Member 1*

**Model answer:** login.jsp posts the form to /login. LoginServlet.doPost reads the parameters, and UserDAO.authenticate loads users.xml with DOM and compares the SHA-256 hash of the typed password with the stored hash. If wrong, it forwards back to the form with an error. If right, it invalidates the old session, creates a new one, stores the User in it, and redirects to /levels (admins go to the dashboard).

**Examiner follow-up:** Why redirect after login instead of forward?

**Answer:** So pressing Refresh does not resend the password form (Post/Redirect/Get), and the address bar shows the real page.

### 4. I type /OnlineQuiz/levels without logging in. What happens?
*Asked to: Member 1*

**Model answer:** AuthFilter runs before the servlet. It finds no user in the session and redirects to /login?msg=login, which shows 'Please log in first.'

**Examiner follow-up:** How does the filter know which URLs to protect?

**Answer:** The @WebFilter annotation lists the patterns: /levels, /start, /question, /answer, /result, /myresults and /admin/*.

### 5. How are passwords stored? Can you recover a forgotten one?
*Asked to: Member 1*

**Model answer:** Only a SHA-256 hash is stored in users.xml. A hash is one-way, so we cannot recover the password; the user would register again.

**Examiner follow-up:** Is plain SHA-256 good enough for a real system?

**Answer:** No. It is fast and unsalted, so the same password always gives the same hash. Real systems use a salted, slow hash such as BCrypt.

### 6. What is XSS and how do you prevent it?
*Asked to: Member 1*

**Model answer:** XSS means an attacker puts script into input that is later shown to other users. We print user text with <c:out>, which escapes < > and &, so it appears as text and does not run.

**Examiner follow-up:** Where in your app could someone try it?

**Answer:** Full name at registration, and the question text and options that an admin adds. All of them are shown through <c:out>.

### 7. Why are your JSP files inside WEB-INF?
*Asked to: Member 1*

**Model answer:** Files under WEB-INF cannot be requested by URL. Every page must go through a servlet and the filter, so /login.jsp gives a 404.

**Examiner follow-up:** Then how does a servlet show a JSP?

**Answer:** request.getRequestDispatcher("/WEB-INF/views/x.jsp").forward(request, response) - the server forwards internally.

### 8. How is the admin login different from the student login?
*Asked to: Member 1*

**Model answer:** It has its own URL (/adminlogin) and JSP, but the same LoginServlet. The servlet accepts only admins on that form and only students on the student form. Pages under /admin/* are protected by the filter, which also checks the role.

**Examiner follow-up:** Can a student open /admin/dashboard?

**Answer:** No. The filter sees a logged-in user who is not an admin and redirects to /adminlogin?msg=denied.

### 9. How does the 10-second timer work? Can a student cheat?
*Asked to: Member 2*

**Model answer:** The server stores quizStartedAt in the session and sends millisLeft to the page. timer.js counts down and submits the form automatically at 0. AnswerServlet rejects answers that arrive later than 10 seconds plus a 2-second grace, so switching JavaScript off gives no extra time.

**Examiner follow-up:** What if the student's computer clock is wrong?

**Answer:** It does not matter. JavaScript only measures time since the page loaded (deadline = now + millisLeft). The real check uses the server's clock.

### 10. Where is the score stored? What if two students play at once?
*Asked to: Member 2*

**Model answer:** In session attributes such as quizScore and quizIndex. A session belongs to one browser, and we use no static variables, so students cannot affect each other.

**Examiner follow-up:** What if one student opens the quiz in two tabs?

**Answer:** Both tabs share one session, so there is only one quiz state. Starting a new quiz in one tab replaces it, and an answer from an old page is ignored because its question number no longer matches. So it is one quiz per login at a time.

### 11. What happens if I press Refresh on a question?
*Asked to: Member 2*

**Model answer:** /question is a read-only GET. It shows the same question and works out the remaining time from the stored start time, so the timer continues and nobody gets extra time.

**Examiner follow-up:** And if I refresh right after clicking Next?

**Answer:** /answer ends with a redirect to /question, so the browser's last request is a GET. Refresh repeats only that GET.

### 12. What does the Back button do in the middle of a quiz?
*Asked to: Member 2*

**Model answer:** Protected pages are sent with no-cache headers, so the browser asks the server again and gets the current question. If an old form is somehow submitted, its hidden qIndex does not match quizIndex, so AnswerServlet ignores it and redirects to the current question.

**Examiner follow-up:** And Back after the quiz is finished?

**Answer:** /question sees quizFinished = true and redirects to /result.

### 13. Why is /answer a POST and /question a GET?
*Asked to: Member 2*

**Model answer:** GET should not change anything. /answer changes the score and the question index, so it is a POST.

**Examiner follow-up:** What happens if I type /answer in the address bar?

**Answer:** That sends a GET. doGet in AnswerServlet simply redirects to /question.

### 14. How do you choose the questions for a quiz?
*Asked to: Member 2*

**Model answer:** StartQuizServlet calls QuestionDAO.getByLevel, shuffles the list with Collections.shuffle, takes subList(0, 10), and stores it in the session.

**Examiner follow-up:** What if a level has fewer than 10 questions?

**Answer:** Math.min takes all of them. If a level has none, the student is sent back to /levels with a message.

### 15. What is the difference between request.setAttribute and session.setAttribute?
*Asked to: Member 2*

**Model answer:** A request attribute lives for one request, and we use it to hand data to the JSP after a forward. A session attribute lives across requests for that user, and we use it for the logged-in user and the quiz state.

### 16. Explain the servlet life cycle.
*Asked to: Member 2*

**Model answer:** init() runs once when the servlet is created, service() runs for every request and calls doGet or doPost, and destroy() runs when the app stops. There is one instance and many threads, so we keep per-user data in the session and never in servlet fields.

**Examiner follow-up:** Your servlets have DAO fields. Is that safe?

**Answer:** Yes. The DAOs hold no per-user data, and file access is protected by one shared lock.

### 17. How do you read questions.xml with DOM?
*Asked to: Member 3*

**Model answer:** XmlHelper.load uses DocumentBuilderFactory and DocumentBuilder.parse to build a Document tree. QuestionDAO calls getElementsByTagName("question"), loops over the elements, and converts each one to a Question object using childText and the four option nodes.

**Examiner follow-up:** How do you add a question?

**Answer:** Load the document, createElement("question"), set the id and level attributes, add the child elements, appendChild to the root, and save with a Transformer.

### 18. DOM or SAX? Why did you choose DOM?
*Asked to: Member 3*

**Model answer:** DOM loads the whole file as a tree in memory, so we can read and change it. SAX is an event stream: low memory but read-only. Our files are small and we need to write, so DOM fits.

### 19. Why do your DAO methods use synchronized (XmlHelper.LOCK)?
*Asked to: Member 3*

**Model answer:** Saving is read, change, write. Without the lock, two requests could read the same file, each add a record, and the second save would erase the first. The lock lets one request at a time do all three steps.

**Examiner follow-up:** Would that work with two Tomcat servers?

**Answer:** No. A lock only works inside one JVM. Several servers need a real database with transactions.

### 20. When is a result saved, and how do you avoid duplicates?
*Asked to: Member 3*

**Model answer:** AnswerServlet saves it once, right after the last answer, and sets quizFinished. It then redirects to /result, which only reads the session. A refresh repeats the GET, so nothing is saved twice. AnswerServlet also returns early if quizFinished is already true.

### 21. Why is the data folder outside the project?
*Asked to: Member 3*

**Model answer:** Redeploying replaces the project folder and would delete users and results. AppInitListener runs at startup, picks OnlineQuizData in the user's home folder (or the dataDir setting in web.xml), and copies the starter XML files from WEB-INF/seed if they are missing.

### 22. What if an XML file is corrupted or missing?
*Asked to: Member 3*

**Model answer:** XmlHelper.load throws a DataException, and web.xml sends any uncaught exception to error.jsp, so the user sees a friendly page and the details stay in the Tomcat log. Saving writes to a .tmp file and then moves it, so a crash mid-save cannot leave a half-written file. A missing file is recreated from the seed on the next start.

### 23. What is the difference between Tomcat 9 and Tomcat 10?
*Asked to: Member 3*

**Model answer:** Tomcat 9 uses javax.servlet and Tomcat 10 and later use jakarta.servlet, because the packages were renamed when Java EE moved to the Eclipse Foundation. That is why the repository has a main branch for Tomcat 10.1 and a tomcat9 branch.

### 24. What is web.xml and does your project need it?
*Asked to: Member 3*

**Model answer:** It is the deployment descriptor. Ours holds the welcome page, session timeout, error pages and the data folder setting. Servlets are registered with annotations, so their mappings are not in it.

**Examiner follow-up:** What would a servlet mapping in web.xml look like?

**Answer:** A <servlet> element with a name and class, plus a <servlet-mapping> element with the same name and a <url-pattern>.

### 25. What did you reuse from the Swing version, and what changed?
*Asked to: Whole team*

**Model answer:** We kept the ideas: three levels, a 10-second timer, a progress bar, score and percentage, and restart. The Swing Timer became a JavaScript countdown plus a server check, the index and score fields became session attributes, JProgressBar became an HTML bar, JOptionPane became result.jsp, and restartGame became /levels plus a new /start. We added login, an admin panel, XML storage and a results history.

**Examiner follow-up:** Name two problems in the Swing version.

**Answer:** It had only 3 questions per level written inside the code, and chooseLevel throws a NullPointerException if the dialog is cancelled. Restarting also starts a new timer without stopping the old one.

## Score card
Copy this table for each mock run.

| # | Score (0-2) | # | Score (0-2) | # | Score (0-2) | # | Score (0-2) | # | Score (0-2) |
|---|---|---|---|---|---|---|---|---|---|
| 1 |  | 6 |  | 11 |  | 16 |  | 21 |  |
| 2 |  | 7 |  | 12 |  | 17 |  | 22 |  |
| 3 |  | 8 |  | 13 |  | 18 |  | 23 |  |
| 4 |  | 9 |  | 14 |  | 19 |  | 24 |  |
| 5 |  | 10 |  | 15 |  | 20 |  | 25 |  |

**Ready** = 40 or more out of 50 with no zero. See also `VIVA_GUIDE.md` (more questions per member) and `VIVA_CHEATSHEET.md` (one-page revision).
