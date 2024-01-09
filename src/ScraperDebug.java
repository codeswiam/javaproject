// this class helps us only print some messages when debug mode is on

public class ScraperDebug {
    public static boolean debug = false;

    public static void setDebug(boolean debug) {
        ScraperDebug.debug = debug;
    }

    public static  void setDebug() {
        ScraperDebug.debug = true;
    }

    public static void debugPrint(String message) {
        if(!ScraperDebug.debug) return;
        System.out.println(message);
    }
}
