# NetBeans Setup Guide (Tomcat 10.1 + JDK 17)

NetBeans opens Maven projects directly, so this project (it has a `pom.xml`) needs no manual library setup.
The dialog names below are from recent NetBeans versions (Apache NetBeans 20 or newer); older ones are very similar.

## Part 1: Install the tools (once per computer)

| Tool | Get it | Check |
|---|---|---|
| JDK 17 | adoptium.net (Temurin 17) | `java -version` shows 17 |
| Apache NetBeans | netbeans.apache.org > Download (installer or zip, version 20+) | starts and lists JDK 17 |
| Tomcat 10.1.x | tomcat.apache.org > Download > Tomcat 10 > **Core: zip** | unzip to `C:\tomcat10` |

If NetBeans does not find JDK 17 at startup, run it with `--jdkhome "C:\Program Files\Eclipse Adoptium\jdk-17..."`, or set `netbeans_jdkhome` in `netbeans\etc\netbeans.conf`.

## Part 2: Register Tomcat in NetBeans

1. Open the **Services** tab (Window > Services).
2. Right-click **Servers > Add Server...**
3. Choose **Apache Tomcat or TomEE**, click **Next**.
4. **Server Location:** browse to `C:\tomcat10`.
5. **Username/Password:** NetBeans asks for a Tomcat manager user. Enter a username and password of your own, and keep "Create user if it does not exist" ticked. Click **Finish**.

You should see **Apache Tomcat or TomEE** under Servers in the Services tab.

If NetBeans reports that the Tomcat version is not supported, update NetBeans. Tomcat 10.1 needs a recent release.

## Part 3: Open the project

1. Get the code: `git clone https://github.com/gillmreet01/OnlineQuiz.git` or unzip the download.
2. **File > Open Project...**, select the `OnlineQuiz` folder. It shows a Maven icon. Click **Open Project**.
3. Wait for the bottom-right progress bar to finish while NetBeans loads Maven.
4. Open the **Projects** tab. You should see these nodes: *Web Pages*, *Source Packages*, *Dependencies*, *Project Files*.
   - **Web Pages** holds `index.jsp`, `css`, `js` and `WEB-INF`.
   - **Source Packages** holds `com.quiz.dao`, `filter`, `listener`, `model`, `servlet` and `util`.

If the project node is red, right-click it and choose **Resolve Project Problems...**, or **Reload POM**. You need internet once to download Maven's dependencies.

## Part 4: Set the project to use Tomcat 10.1

1. Right-click the project > **Properties**.
2. **Run** category > **Server:** choose **Apache Tomcat or TomEE** (the one from Part 2).
3. **Java EE Version:** choose **Jakarta EE 10 Web** (or the highest Jakarta EE option).
4. **Relative URL / Context Path:** `/OnlineQuiz`.
5. **Sources** category > **Source/Binary Format:** 17. Click **OK**.

## Part 5: Run it

1. Right-click the project > **Run** (or press F6).
2. The first run builds the WAR and starts Tomcat. The **Output** tab shows `OnlineQuiz data folder: ...` when the app starts.
3. NetBeans opens your browser at `http://localhost:8080/OnlineQuiz/`. If it opens the wrong URL, enter that one yourself.
4. Log in as `student1` / `student123`, start a quiz and watch the 10-second timer move to the next question.

When you change code, press **F6** again. NetBeans redeploys.

## Alternative: run without registering Tomcat

Open the **Projects** tab, right-click the project > **Run Maven > Goals...**, type `cargo:run` and click OK. This downloads Tomcat 10.1 on the first run. Open `http://localhost:8080/OnlineQuiz/`. Stop it with the red square in the Output tab.

## Troubleshooting

| Problem | Fix |
|---|---|
| `package jakarta.servlet does not exist` | The Maven dependencies have not loaded. Right-click project > **Reload POM** or **Build with Dependencies**. Check you have internet. |
| Tomcat not accepted / "unsupported version" | Update NetBeans to 20 or newer, and use Tomcat 10.1.x. |
| **Server startup error: port 8080 in use** | Stop other Tomcats, or change the port (Services > Servers > Tomcat > Properties > Connection). |
| HTTP 404 | The URL must include `/OnlineQuiz/`. Check the Context Path in Project Properties > Run. |
| `ClassNotFoundException: javax.servlet` | You are using Tomcat 9. Use Tomcat 10.1, or follow the Tomcat 9 section in `README.md`. |
| Changes not showing | **Clean and Build** (Shift+F11), then **Run**. |
| Fresh start with no registered users | Stop the server and delete the `OnlineQuizData` folder in your user home. |
