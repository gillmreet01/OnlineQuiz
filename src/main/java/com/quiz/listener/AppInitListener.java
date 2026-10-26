package com.quiz.listener;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

import com.quiz.dao.XmlHelper;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Runs ONCE when Tomcat starts the application.
 * Job: decide where the XML data folder is, and copy the starter XML files there
 * (from WEB-INF/seed) if they do not exist yet.
 *
 * Why a folder outside the project? When you redeploy from Eclipse, the project folder is
 * replaced and any registered users / added questions would be lost.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    private static final String[] DATA_FILES = {"questions.xml", "users.xml", "results.xml"};

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // 1. Folder from web.xml <context-param>, or default: <your home folder>/OnlineQuizData
        String configured = context.getInitParameter("dataDir");
        File dataDir;
        if (configured == null || configured.trim().isEmpty()) {
            dataDir = new File(System.getProperty("user.home"), "OnlineQuizData");
        } else {
            dataDir = new File(configured.trim());
        }
        dataDir.mkdirs();
        XmlHelper.setDataDir(dataDir);

        // 2. Copy starter files that are missing
        for (String name : DATA_FILES) {
            File target = new File(dataDir, name);
            if (!target.exists()) {
                try (InputStream in = context.getResourceAsStream("/WEB-INF/seed/" + name)) {
                    Files.copy(in, target.toPath());
                } catch (Exception e) {
                    throw new RuntimeException("Could not create " + target, e);
                }
            }
        }
        context.log("OnlineQuiz data folder: " + dataDir.getAbsolutePath());
    }
}
