package main;

import debug.ScraperDebug;
import gui.JobPortalScraperUI;

public class Main {
    public static void main(String[] args) {
        ScraperDebug.setDebug();
        new JobPortalScraperUI();
    }
}
