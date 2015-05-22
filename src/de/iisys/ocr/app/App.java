package de.iisys.ocr.app;

import de.iisys.ocr.config.Options;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * App
 * de.iisys.ocr.app
 * Created by reza on 27.11.14.
 */
public abstract class App {
    private final Options options;
    private final Map<String, String> configs;

    protected abstract void run();

    public App(Options options) {
        this.options = options;
        configs = new HashMap<String, String>();
    }

    // <editor-fold desc="Arguments">

    protected boolean hasOption(String optionName) {
        return (options.getSet().isSet("a"));
    }

    protected String getOption(String optionName) {
        if (options.getSet().isSet(optionName)) {
            return options.getSet().getOption("log").getResultValue(0);
        } else {
            return null;
        }
    }

    // </editor-fold>

    // <editor-fold desc="Config">

    public void readConfig(String configFile) throws ParserConfigurationException, IOException, SAXException {
        File file = new File(configFile);

        if (!file.exists()) {
            throw new RuntimeException("Config file does not exists");
        }

        // Open config file
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(file);
        document.getDocumentElement().normalize();

        // Read configs
        NodeList nodeList = document.getElementsByTagName("config");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node nNode = nodeList.item(i);
            Element eElement = (Element)nNode;

            String name = eElement.getAttribute("name");
            String value = eElement.getAttribute("value");

            if (configs.containsKey(name)) {
                throw new RuntimeException("Duplicate name in the config file.");
            }

            configs.put(name, value);
        }

    }

    protected boolean hasConfig(String configName) {
        return configs.containsKey(configName);
    }

    protected String getConfig(String configName) {
        return configs.get(configName);
    }

    // </editor-fold>

    public void execute() {
        run();
    }

    public boolean check() {
        // TODO: check for app specific option error
        return true;
    }

    public String getCheckErrors() {
        // TODO: return app specific option error
        return "";
    }

}
