# Eclipse Import Guide (Eclipse + Tomcat 10.1 + JDK 17)

The dialogs below are text mockups of what you should see. Button names can differ slightly between Eclipse versions.

## Part 0: Get the code
1. Clone or download this repository (Code > Download ZIP) and unzip it, for example to `C:\work\OnlineQuiz`.

## Part 1: Install the tools (once per computer)

| Tool | Get it | Check |
|---|---|---|
| JDK 17 | adoptium.net (Temurin 17) | `java -version` shows 17 |
| Tomcat 10.1.x | tomcat.apache.org > Download > Tomcat 10 > **Core: zip** | unzip to `C:\tomcat10` |
| Eclipse | eclipse.org > **Eclipse IDE for Enterprise Java and Web Developers** (2023-09 or newer) | |

The plain "Eclipse IDE for Java Developers" has no server tools, so the steps below will not work with it.

## Part 2: Tell Eclipse where Tomcat is

**Window > Preferences > Server > Runtime Environments > Add...**

```
┌ New Server Runtime Environment ──────────────────────┐
│ Select the type of runtime environment:              │
│   ▸ Apache                                           │
│       Apache Tomcat v9.0                             │
│    ► Apache Tomcat v10.1      <-- select this        │
│ [x] Create a new local server                        │
│                              [Next >]                │
└──────────────────────────────────────────────────────┘
┌ Tomcat Server ───────────────────────────────────────┐
│ Name:                 Apache Tomcat v10.1            │
│ Tomcat installation directory: [C:\tomcat10]  Browse │
│ JRE:                  [ jdk-17 ]  <-- not 8 or 21    │
│                              [Finish]                │
└──────────────────────────────────────────────────────┘
```

If JDK 17 is not in the JRE list, click **Installed JREs...**, then **Add > Standard VM**, choose the JDK 17 folder, and finish. Click **Apply and Close**.

If "Apache Tomcat v10.1" is missing from the list, your Eclipse is too old. Update Eclipse.

## Part 3: Create the Dynamic Web Project

**File > New > Dynamic Web Project** (or File > New > Other > Web > Dynamic Web Project)

**Page 1**
```
┌ New Dynamic Web Project ─────────────────────────────┐
│ Project name:  OnlineQuiz        <-- exactly this    │
│ Target runtime:  [Apache Tomcat v10.1]               │
│ Dynamic web module version:  [6.0]                   │
│ Configuration:  Default Configuration                │
│                                [Next >]              │
└──────────────────────────────────────────────────────┘
```

**Page 2 (Java)**
```
│ Source folders on build path:                        │
│    src/main/java        <-- must be the source folder│
│ Default output folder: build/classes                 │
│                                [Next >]              │
```
This page differs by version. Whatever it shows, the goal is that `src/main/java` is the source folder. If it already shows `src/main/java`, click Next.

**Page 3 (Web Module)**
```
│ Context root:       OnlineQuiz                       │
│ Content directory:  src/main/webapp                  │
│ [ ] Generate web.xml deployment descriptor           │
│                                 [Finish]             │
```
Make sure **Generate web.xml is unticked**. If you are asked about opening the Java EE perspective, click **Open Perspective**.

## Part 4: Copy the code in

The new project in **Project Explorer** should look like this:
```
OnlineQuiz
├─ Deployment Descriptor: OnlineQuiz
├─ Java Resources
│   └─ src/main/java        (empty)
├─ build
└─ src
    └─ main
        ├─ java
        └─ webapp           (contains META-INF and WEB-INF)
```

1. In File Explorer open the downloaded `src\main\java`. Drag the **`com`** folder onto **`src/main/java`** in Eclipse. If a dialog asks, choose **Copy files and folders**.
2. Open the downloaded `src\main\webapp`. Select **everything inside it** (`css`, `js`, `WEB-INF`, `index.jsp`) and drag it onto **`src/main/webapp`** in Eclipse. If asked about a conflict or overwriting, choose **Yes to All**.
3. Click the project and press **F5** (Refresh).

Check: `src/main/webapp/WEB-INF` should contain `lib`, `seed`, `views` and `web.xml`. `src/main/java` should show the packages `com.quiz.dao`, `filter`, `listener`, `model`, `servlet` and `util`.

## Part 5: Check there are no red errors

1. **Project > Clean... > Clean all projects > OK**.
2. Open the **Problems** tab (Window > Show View > Problems). It should show **0 errors**. Yellow warnings are fine.
3. If `jakarta.servlet` is underlined red:
   - Right-click the project > **Properties > Java Build Path > Libraries**. You should see **Server Runtime [Apache Tomcat v10.1]** and **Web App Libraries**.
   - If the server runtime is missing, go to **Properties > Targeted Runtimes**, tick **Apache Tomcat v10.1**, and click **Apply**.
4. In Properties, open **Java Compiler** and set the compliance level to **17**.

## Part 6: Run it

1. Right-click the project > **Run As > Run on Server**.
```
┌ Run On Server ───────────────────────────────────────┐
│ ( ) Choose an existing server                        │
│    ► Tomcat v10.1 Server at localhost   <-- select   │
│ [x] Always use this server                           │
│                                [Next >]              │
│ Available:  OnlineQuiz  --> [Add >]  --> Configured  │
│                                [Finish]              │
└──────────────────────────────────────────────────────┘
```
2. Wait for the **Console** to print lines like these:
```
INFO: OnlineQuiz data folder: C:\Users\you\OnlineQuizData
INFO: Server startup in [3456] milliseconds
```
3. Open Chrome and go to **`http://localhost:8080/OnlineQuiz/`**.
4. You should see the dark landing page with "Test your knowledge" and three buttons.

## Part 7: Quick smoke test (2 minutes)

| Do this | Expect |
|---|---|
| Click **Student Login**, enter `student1` / `student123` | The level page with Easy / Medium / Hard cards |
| Click **Start Easy** | Question 1 of 10, a countdown near 10s, and a draining bar |
| Do nothing for 10 s | It moves to question 2 by itself |
| Finish the quiz | The result ring with the percentage, and a green/red review |
| Click **My Results** | One row in the table |
| Logout, go to `…/OnlineQuiz/adminlogin`, log in as `admin` / `admin123` | The dashboard |
| Click **Add Question** and save one | "Question added successfully!" |

Then open `C:\Users\<you>\OnlineQuizData\` and open `results.xml` or `questions.xml` in Eclipse. Your new data is in those files, which is a good thing to show in the demo.

## Troubleshooting

| Problem | Fix |
|---|---|
| **HTTP 404** at `/OnlineQuiz/` | Check that the URL includes `/OnlineQuiz/`. Look in the Console for red errors during startup. |
| **Port 8080 already in use** | Stop other Tomcats (Servers tab, red square). Or double-click the server in the Servers tab and change the HTTP port to 8081. |
| **Red error on `<%@ taglib ... jakarta.tags.core %>`** | The two JSTL jars must be in `WEB-INF/lib`. Refresh with F5, then run Project > Clean. |
| **`ClassNotFoundException: javax.servlet...`** | You installed Tomcat 9 but used this Tomcat 10 code. Use Tomcat 10.1, or follow the Tomcat 9 section in `README.md`. |
| **"Dynamic web module 6.0" not offered** | Your Eclipse is too old. Install 2023-09 or newer. |
| **Changes don't show up** | In the Servers tab, right-click the server > **Clean...** and then **Publish**. |
| **Want a fresh start with no registered users** | Stop the server and delete the `OnlineQuizData` folder in your user home. It is recreated on the next start. |

Each team member should do Parts 2-7 on their own laptop at least once before the viva.
