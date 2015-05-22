package de.iisys.ocr;

import de.iisys.ocr.app.App;
import de.iisys.ocr.app.AppFactory;
import de.iisys.ocr.config.Options;
import de.iisys.ocr.config.Options.Separator;
import de.iisys.ocr.config.Options.Multiplicity;

/**
 * Runner
 * de.iisys.ocr
 * Created by reza on 25.07.14.
 */
public class Runner {
    private static void printUsage() {
        System.out.println(Runner.class.toString() + " [-a] [-log=<log file>] [-config=<config file>] -mode=<mode>");
    }

    /**
     * @param args refer to usage
     */
    public static void main(String[] args) {
        Options options = new Options(args, 0);

        options.getSet().addOption("a", Multiplicity.ZERO_OR_ONE);
        options.getSet().addOption("log", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
        options.getSet().addOption("config", Separator.EQUALS, Multiplicity.ZERO_OR_ONE);
        options.getSet().addOption("mode", Separator.EQUALS, Multiplicity.ONCE);

        if (!options.check()) {
            System.err.println(options.getCheckErrors());
            printUsage();
            System.exit(1);
        }

        String executionMode = options.getSet().getOption("mode").getResultValue(0);
        System.out.println("Execution mode: " + executionMode);

        String configFile = "config.xml";
        if (options.getSet().isSet("config")) {
            configFile = options.getSet().getOption("config").getResultValue(0);
        }

        AppFactory appFactory = new AppFactory(options, configFile);
        App app = appFactory.generateApp(executionMode);

        if (app == null) {
            System.err.println("Invalid execution mode.");
            printUsage();
            System.exit(1);
        }

        if (!app.check()) {
            System.err.println(app.getCheckErrors());
            printUsage();
            System.exit(1);
        }

        app.execute();
    }
}
