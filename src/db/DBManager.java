package db;

import debug.ScraperDebug;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBManager {

    public static DBManager dbManagerInstance = null;

    public static DBManager getInstance() {
        if(dbManagerInstance == null) {
            dbManagerInstance = new DBManager();
        }
        return dbManagerInstance;
    }

    public Connection makeConnection() throws SQLException {
        return DriverManager.getConnection(this.connectionUrl, this.dbUsername, this.dbPassword);
    }

    protected String configFilePath;
    protected String dbName;
    protected String dbUsername;
    protected String dbPassword;
    protected String dbHost;
    protected String dbPort;
    protected String connectionUrl;
    private DBManager() {
        this.dbName = "jobportalscraper";
        this.dbUsername = "root";
        this.dbPassword = "";
        this.configFilePath = ".dbconf";
        this.loadDatabaseConfig();
        this.connectionUrl = "jdbc:mysql://"+this.dbHost+":"+this.dbPort+"/" + this.dbName;
        try {
            this.makeConnection();
            ScraperDebug.debugPrint("Connected successfully");
        } catch (SQLException e) {
            ScraperDebug.debugPrint("Database connection Error: " + e.getMessage());
        }
    }

    protected void loadDatabaseConfig() {
        Scanner reader = null;
        try {
            File config = new File(this.configFilePath);
            ScraperDebug.debugPrint(config.getAbsolutePath());
            reader = new Scanner(config);
            while(reader.hasNextLine()) {
                String line = reader.nextLine();
                if(line.contains("db_name")) {
                    line = line.replace("db_name=", "").replace("\"", "").trim();
                    this.dbName = line;
                } else if(line.contains("db_username")) {
                    line =line.replace("db_username=", "").replace("\"", "").trim();
                    this.dbUsername = line;
                } else if(line.contains("db_password")) {
                    line =line.replace("db_password=", "").replace("\"", "").trim();
                    this.dbPassword = line;
                } else if(line.contains("db_host")) {
                    line =line.replace("db_host=", "").replace("\"", "").trim();
                    this.dbHost = line;
                } else if(line.contains("db_port")) {
                    line =line.replace("db_port=", "").replace("\"", "").trim();
                    this.dbPort = line;
                }
            }
        } catch (Exception e) {
            ScraperDebug.debugPrint(e.getMessage());
            //e.printStackTrace();
        }

    }
}

