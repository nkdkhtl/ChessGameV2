package utils;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class SettingsManager {
    private static final String SETTINGS_FILE = "settings.xml";
    private static Document settingsDocument;

    static {
        loadSettings();
    }

    public static void saveSettings(String key, String value) {
        if (settingsDocument == null) {
            settingsDocument = createNewDocument();
        }

        NodeList nodeList = settingsDocument.getElementsByTagName(key);
        if (nodeList.getLength() > 0) {
            nodeList.item(0).setTextContent(value);
        } else {
            Element newElement = settingsDocument.createElement(key);
            newElement.setTextContent(value);
            settingsDocument.getDocumentElement().appendChild(newElement);
        }

        saveDocumentToFile();
    }

    public static String getSetting(String key, String defaultValue) {
        if (settingsDocument == null) {
            return defaultValue;
        }

        NodeList nodeList = settingsDocument.getElementsByTagName(key);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return defaultValue;
        }
    }

    private static void loadSettings() {
        try {
            File file = new File(SETTINGS_FILE);
            if (!file.exists()) {
                settingsDocument = createNewDocument();
                return;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            settingsDocument = builder.parse(file);
        } catch (Exception e) {
            // If the settings file does not exist, use default settings
            settingsDocument = createNewDocument();
        }
    }

    private static Document createNewDocument() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element root = document.createElement("settings");
            document.appendChild(root);
            return document;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveDocumentToFile() {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(settingsDocument);
            StreamResult result = new StreamResult(new File(SETTINGS_FILE));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}