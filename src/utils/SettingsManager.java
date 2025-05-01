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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import GameLauncher.GameLauncher;
import bot.Bot;

public class SettingsManager {
    private static final String SETTINGS_FILE = "settings.xml";
    private static Document settingsDocument;

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
            loadSettings(); // Ensure settings are loaded
        }

        NodeList nodeList = settingsDocument.getElementsByTagName(key);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return defaultValue;
        }
    }

    public static void loadSettings() {
        try {
            File file = new File(SETTINGS_FILE);
            if (!file.exists()) {
                settingsDocument = createNewDocument();
                return;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(SETTINGS_FILE);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("settings");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    String theme = eElement.getElementsByTagName("theme").item(0).getTextContent();
                    String sound = eElement.getElementsByTagName("sound").item(0).getTextContent();
                    String clockStr = eElement.getElementsByTagName("clock").item(0).getTextContent();
                    String botColor = eElement.getElementsByTagName("botColor").item(0).getTextContent();
                    int clock = Integer.parseInt(clockStr.split(" ")[0]); 
                    
                    ThemeManager.setTheme(theme);
                    SoundManager.setSoundSate(sound);
                    GameLauncher.getBoard().setDuration(clock);
                    Bot.setBotColor((botColor.equals("white")) ? true : false);
                }
            }

            settingsDocument = doc; 
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