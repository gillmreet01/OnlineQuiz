package com.quiz.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Small helper that does the boring DOM work for all three DAOs:
 *   - find the XML file,
 *   - parse a file into a Document (read),
 *   - write a Document back to the file (save).
 */
public class XmlHelper {

    /**
     * Only one request at a time may read-modify-write an XML file.
     * Every DAO method wraps its work in synchronized (XmlHelper.LOCK).
     * Without this, two students registering at the same moment could overwrite each other.
     */
    public static final Object LOCK = new Object();

    private static File dataDir; // folder that holds the 3 XML files

    public static void setDataDir(File dir) {
        dataDir = dir;
    }

    public static File getDataDir() {
        return dataDir;
    }

    /** Full path of one data file, e.g. getFile("users.xml"). */
    public static File getFile(String fileName) {
        return new File(dataDir, fileName);
    }

    /** READ: parse the XML file into a DOM tree (the whole file is loaded in memory). */
    public static Document load(File file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // Security: refuse files containing a DOCTYPE (blocks XXE attacks).
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            Document doc = factory.newDocumentBuilder().parse(file);
            removeBlankText(doc.getDocumentElement());
            return doc;
        } catch (Exception e) {
            throw new DataException("Could not read " + file.getName(), e);
        }
    }

    /**
     * WRITE: turn the DOM tree back into XML text.
     * We write to a temporary file first and then replace the real file,
     * so a crash in the middle of writing cannot leave a half-written XML file.
     */
    public static void save(Document doc, File file) {
        File temp = new File(file.getPath() + ".tmp");
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            try (OutputStream out = new FileOutputStream(temp)) {
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }
            Files.move(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new DataException("Could not save " + file.getName(), e);
        }
    }

    /** Text inside the first child element with this tag, e.g. <role>admin</role> gives "admin". */
    public static String childText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) {
            return "";
        }
        return list.item(0).getTextContent();
    }

    /** Creates <tagName>value</tagName> and appends it to parent. */
    public static void addChild(Document doc, Element parent, String tagName, String value) {
        Element child = doc.createElement(tagName);
        child.setTextContent(value); // DOM escapes characters like < and & for us
        parent.appendChild(child);
    }

    /**
     * Removes the blank lines/indentation between tags.
     * Without this, every save would add extra empty lines to the file.
     */
    private static void removeBlankText(Node node) {
        NodeList children = node.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                node.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeBlankText(child);
            }
        }
    }
}
