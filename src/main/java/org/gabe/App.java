package org.gabe;

import org.jade.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main( String[] args ) {
        if (args.length > 0 && "faulty".equals(args[0])) {
            logger.warn("faulty mode");
            Window.faulty = true;
        }

        Window.getInstance().run();
    }
}
