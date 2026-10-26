# IntelliJ IDEA Setup Guide (Tomcat 10.1 + JDK 17)

## Quick path: open the pom.xml (works in Community and Ultimate)

The project has a `pom.xml`, so IntelliJ sets up the source folders and libraries by itself.

1. Install **JDK 17**. Clone the repo (`git clone https://github.com/gillmreet01/OnlineQuiz.git`) or unzip the download.
2. **File > Open**, select the **`pom.xml`** file (or the `OnlineQuiz` folder), choose **Open as Project**, click **Trust Project**.
3. Wait for the Maven import to finish (progress bar at the bottom). Set **File > Project Structure > Project > SDK** to JDK 17 if asked.
4. Open the **Maven** tool window (right edge) > **OnlineQuiz > Plugins > cargo > cargo:run** and double-click it.
   - The first run downloads Tomcat 10.1 (about 12 MB), so you do not need to install Tomcat. It needs internet once.
   - Wait for `Press Ctrl+C to stop the container`, then open **http://localhost:8080/OnlineQuiz/**.
5. Stop it with the red square in the Run window. After you change code, stop and run `cargo:run` again.

Command-line equivalent: `mvn cargo:run` in the project folder.
Make a WAR for standalone Tomcat: `mvn package` creates `target/OnlineQuiz.war`.

If the quick path works, you can skip the rest of this file. The rest is the manual setup (no Maven) and troubleshooting.

---

# Manual setup without Maven

Use this only if you do not want Maven. You open the project as a plain folder and tell IntelliJ about the source folder, the libraries and Tomcat.
Do the common steps first, then follow **Path A** (IntelliJ Ultimate) or **Path B** (Community).

## Which IntelliJ do I have?

| Edition | Tomcat built in? | Use |
|---|---|---|
| **Ultimate** (free for students at jetbrains.com/student) | Yes | Path A |
| **Community** | No | Path B (free "Smart Tomcat" plugin) |

## Common steps

1. Install **JDK 17** and unzip **Tomcat 10.1.x** (Core zip) to, for example, `C:\tomcat10`.
2. Clone the repo (`git clone https://github.com/gillmreet01/OnlineQuiz.git`) or unzip the download.
3. **File > Open**, choose the `OnlineQuiz` folder, click **Trust Project**.
4. **File > Project Structure > Project**: set **SDK** to JDK 17 (Add SDK > Download JDK or point to your install) and **Language level** to 17.
5. **Project Structure > Modules**: if no module exists, click **+ > New Module > Java**, set the **Content root** to the `OnlineQuiz` folder.
   - Open the **Sources** tab, right-click `src/main/java` in the tree and click **Sources** (the folder turns blue).
6. **Project Structure > Libraries > + > Java**: choose `src/main/webapp/WEB-INF/lib` (the two JSTL jars) and add it to the module.
7. Add the servlet API: **Libraries > + > Java**, choose `C:\tomcat10\lib\servlet-api.jar`, add it to the module. (Ultimate: you can instead add Tomcat as the module's server library in Path A.)
8. Click **Apply**. The red `jakarta.servlet` imports in `src/main/java` should now be gone.

## Path A: IntelliJ Ultimate

1. **Project Structure > Modules > + (on the module) > Web**. Set:
   - **Deployment descriptor:** `src/main/webapp/WEB-INF/web.xml`
   - **Web resource directory:** `src/main/webapp`
2. **Project Structure > Artifacts > + > Web Application: Exploded > From Module 'OnlineQuiz'**. Click OK.
3. **Run > Edit Configurations > + > Tomcat Server > Local**:
   - **Application server:** Configure... > pick `C:\tomcat10`
   - **JRE:** 17
   - **Deployment** tab: **+ > Artifact > OnlineQuiz:war exploded**
   - **Application context:** `/OnlineQuiz`
   - **Server** tab: HTTP port `8080`
4. Click **Run** (green triangle). The **Services** tool window shows Tomcat starting.

## Path B: IntelliJ Community + Smart Tomcat

1. **File > Settings > Plugins > Marketplace**, search **Smart Tomcat**, click **Install**, restart IntelliJ.
2. **Run > Edit Configurations > + > Smart Tomcat** and set:
   - **Tomcat Server:** Configure... > **+** > choose `C:\tomcat10`
   - **Deployment Directory:** `<project>\src\main\webapp`
   - **Context Path:** `/OnlineQuiz`
   - **Server Port:** `8080`
   - **Use classpath of module:** `OnlineQuiz`
3. Click **Run**.

Community highlights JSP files only as plain text and has no JSP code completion. That does not affect running the app.

## Check it works

1. Open **http://localhost:8080/OnlineQuiz/**. You should see the dark landing page.
2. Log in as `student1` / `student123`, start a quiz, and watch the 10-second timer move to the next question.
3. The Run window prints `OnlineQuiz data folder: ...`. Your XML data lives there (default: `OnlineQuizData` in your user folder).

## Troubleshooting

| Problem | Fix |
|---|---|
| `jakarta.servlet` red | Step 7 not done, or Tomcat 9 chosen. Use Tomcat **10.1** and add its `servlet-api.jar`. |
| HTTP 404 | The context path must be `/OnlineQuiz` and the URL must include it. Check the log for startup errors. |
| JSTL / `<c:...>` error | The two jars in `WEB-INF/lib` must be added as a library (step 6) and be present in the deployed app. |
| Port 8080 in use | Stop other Tomcats, or change the port in the run configuration. |
| Changes not showing | Rebuild (**Build > Rebuild Project**), then restart the server. |
| Fresh start with no users | Stop the server and delete the `OnlineQuizData` folder in your user home. |
