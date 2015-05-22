package de.iisys.ocr.app;

import de.iisys.ocr.config.Options;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * de.iisys.ocr.app
 * Created by reza on 27.11.14.
 */
public class AppFactory {
    private final Options options;
    private final String configFile;

    public AppFactory(Options options, String configFile) {
        this.options = options;
        this.configFile = configFile;
    }

    public App generateApp(String mode) {
        App app = null;

        if (mode.equals("correct")) {
            app = new CorrectorApp(options);
        }
        else if (mode.equals("ocr-corpus")) {
            app = new OCRCorpusApp(options);
        }

        try {
            app.readConfig(configFile);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        }

        return app;
    }
}
