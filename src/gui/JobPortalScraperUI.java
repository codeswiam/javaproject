package gui;

import db.DBManager;
import debug.ScraperDebug;
import gui.visualization.Colors;
import gui.visualization.VisualizationUI;
import scrapers.EmploiScraper;
import scrapers.MjobScraper;
import scrapers.RekruteScraper;
import scrapers.ScraperListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class JobPortalScraperUI extends JFrame {
    private static final DBManager dbManager = DBManager.getInstance();
    public static final JButton scrapeButton = new JButton("SCRAPE");
    protected EmploiScraper emploiScraper;
    protected RekruteScraper rekruteScraper;
    protected MjobScraper mjobScraper;
    protected JProgressBar progressBar;
    protected JLabel logsLabel;
    protected JLabel scrapedWebsiteLabel;
    protected static int total = 1;
    protected JList<String> websitesList;

    public JobPortalScraperUI() {
        initialize();
        this.setVisible(true);

        ScraperListener scraperListener = new ScraperListener() {
            @Override
            public void updateTotalPages(int pages) {
                logsLabel.setText("Number of pages found: " + pages);
                ScraperDebug.debugPrint("Number of pages found: " + pages);
            }

            @Override
            public void updateTotalPosts(int posts) {
                logsLabel.setText("Number of posts found: " + posts);
                ScraperDebug.debugPrint("Number of posts found: " + posts);
                total = Math.min(posts, EmploiScraper.maxPostsToScrape);
            }

            @Override
            public void updateCurrentPost(int post, String url) {
                int progress = (post * 100) / total;
                logsLabel.setText("Scraping "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Scraping "+progress+"% : " + url);
            }

            @Override
            public void updateCurrentStorage(int current) {
                int progress = (current * 100) / total;
                logsLabel.setText("Storing "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Storing "+progress+"%.");
            }

            @Override
            public void finishedMysqlStorage(int success, int failed) {
                JOptionPane.showMessageDialog(null, "Finished storing: "+success+" success, "+failed+" failed.");
            }
        };

        emploiScraper = new EmploiScraper();
        emploiScraper.setListener(new scrapers.ScraperListener() {
            public void updateTotalPages(int pages) {
                logsLabel.setText("Number of pages found: " + pages);
                ScraperDebug.debugPrint("Number of pages found: " + pages);
            }

            @Override
            public void updateTotalPosts(int posts) {
                logsLabel.setText("Number of posts found: " + posts);
                ScraperDebug.debugPrint("Number of posts found: " + posts);
                total = Math.min(posts, EmploiScraper.maxPostsToScrape);
            }

            @Override
            public void updateCurrentPost(int post, String url) {
                int progress = (post * 100) / total;
                logsLabel.setText("Scraping "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Scraping "+progress+"% : " + url);
            }

            @Override
            public void updateCurrentStorage(int current) {
                int progress = (current * 100) / total;
                logsLabel.setText("Storing "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Storing "+progress+"%.");
            }

            @Override
            public void finishedMysqlStorage(int success, int failed) {
                JOptionPane.showMessageDialog(null, "Finished storing: "+success+" success, "+failed+" failed.");
            }
        });

        rekruteScraper = new RekruteScraper();
        rekruteScraper.setListener(new scrapers.ScraperListener() {
            @Override
            public void updateTotalPages(int pages) {
                logsLabel.setText("Number of pages found: " + pages);
                ScraperDebug.debugPrint("Number of pages found: " + pages);
            }

            @Override
            public void updateTotalPosts(int posts) {
                logsLabel.setText("Number of posts found: " + posts);
                ScraperDebug.debugPrint("Number of posts found: " + posts);
                total = Math.min(posts, RekruteScraper.maxPostsToScrape);
            }

            @Override
            public void updateCurrentPost(int post, String url) {
                int progress = (post * 100) / total;
                logsLabel.setText("Scraping "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Scraping "+progress+"% : " + url);
            }

            @Override
            public void updateCurrentStorage(int current) {
                int progress = (current * 100) / total;
                logsLabel.setText("Storing "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Storing "+progress+"%.");
            }

            @Override
            public void finishedMysqlStorage(int success, int failed) {
                JOptionPane.showMessageDialog(null, "Finished storing: "+success+" success, "+failed+" failed.");
            }
        });
        mjobScraper = new MjobScraper();
        mjobScraper.setListener(new scrapers.ScraperListener() {
            @Override
            public void updateTotalPages(int pages) {
                logsLabel.setText("Number of pages found: " + pages);
                ScraperDebug.debugPrint("Number of pages found: " + pages);
            }

            @Override
            public void updateTotalPosts(int posts) {
                logsLabel.setText("Number of posts found: " + posts);
                ScraperDebug.debugPrint("Number of posts found: " + posts);
                total = Math.min(posts, MjobScraper.maxPostsToScrape);
            }

            @Override
            public void updateCurrentPost(int post, String url) {
                int progress = (post * 100) / total;
                logsLabel.setText("Scraping "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Scraping "+progress+"% : " + url);
            }

            @Override
            public void updateCurrentStorage(int current) {
                int progress = (current * 100) / total;
                logsLabel.setText("Storing "+progress+"%.");
                progressBar.setValue(progress);
                ScraperDebug.debugPrint("Storing "+progress+"%.");
            }

            @Override
            public void finishedMysqlStorage(int success, int failed) {
                JOptionPane.showMessageDialog(null, "Finished storing: "+success+" success, "+failed+" failed.");
            }
        });

    }

    private void initialize() {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setBounds(400, 200, 800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.blue);
        header.setBorder(new EmptyBorder(30, 30, 30, 30));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel("Job Portal Scraper");
        title.setFont(new Font("Futura", Font.BOLD, 50));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));
        main.setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.CENTER);

        JPanel containerContainer = new JPanel();
        containerContainer.setBorder(new EmptyBorder(10, 20, 10, 20));
        containerContainer.setLayout(new GridLayout(2,1));
        containerContainer.setBackground(Colors.white);
        main.add(containerContainer, BorderLayout.CENTER);

        // Website Selection and Scraping Button
        JPanel container = new JPanel();
        container.setBackground(Colors.white);
        containerContainer.add(container);
        container.setLayout(new BorderLayout(10, 10));

        JLabel selectSiteLabel = new JLabel("SELECT WEBSITE");
        selectSiteLabel.setFont(new Font("Montseratt", Font.BOLD, 15));
        selectSiteLabel.setForeground(Colors.blue);
        selectSiteLabel.setHorizontalAlignment(0);
        container.add(selectSiteLabel, BorderLayout.NORTH);

        JPanel listContainer = new JPanel(new GridLayout(1, 3));
        listContainer.setBackground(Colors.white);
        container.add(listContainer, BorderLayout.CENTER);

        JLabel fillerLabel1 = new JLabel();
        listContainer.add(fillerLabel1);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        listModel.addElement("Rekrute.ma");
        listModel.addElement("M-job.ma");
        listModel.addElement("Emploi.ma");

        websitesList = new JList<>(listModel);
        websitesList.setCellRenderer(new CenteredListCellRenderer());
        listContainer.add(websitesList);

        JLabel fillerLabel2 = new JLabel();
        listContainer.add(fillerLabel2);

        // scraping button

        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        buttonPanel.setBackground(Colors.white);
        container.add(buttonPanel, BorderLayout.SOUTH);

        JLabel fillerLabel5 = new JLabel();
        buttonPanel.add(fillerLabel5);

        scrapeButton.setFont(new Font("Montseratt", Font.BOLD, 15));
        scrapeButton.setForeground(Colors.blue);

        scrapeButton.addActionListener(e -> scrape());
        buttonPanel.add(scrapeButton);

        JLabel fillerLabel6 = new JLabel();
        buttonPanel.add(fillerLabel6);

        // Scraping Progress and Logs + Saving to Database
        JPanel scpContainer = new JPanel(new GridLayout(3, 1));
        scpContainer.setBackground(Colors.white);
        containerContainer.add(scpContainer);

        JLabel fillerLabel3 = new JLabel();
        scpContainer.add(fillerLabel3);

        JPanel scrapingContainer = new JPanel();
        scrapingContainer.setBackground(Colors.white);
        scpContainer.add(scrapingContainer);
        scrapingContainer.setLayout(null);

        JPanel labelsContainer = new JPanel(new GridLayout(1, 2));
        labelsContainer.setBounds(0, 10, 700, 20);
        labelsContainer.setBackground(Colors.white);
        labelsContainer.setForeground(Colors.brown);
        scrapingContainer.add(labelsContainer);

        logsLabel = new JLabel("Scraping: 0%");
        labelsContainer.add(logsLabel);

        scrapedWebsiteLabel = new JLabel("Website");
        scrapedWebsiteLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        labelsContainer.add(scrapedWebsiteLabel);

        progressBar = new JProgressBar();
        progressBar.setBounds(0, 30, 700, 30);
        progressBar.setBackground(Colors.green);
        scrapingContainer.add(progressBar);

        JLabel fillerLabel4 = new JLabel();
        scpContainer.add(fillerLabel4);
        
        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.blue);
        footer.setBorder(new EmptyBorder(30, 30, 30, 30));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        JLabel name1 = new JLabel("Chagour Maryam");
        name1.setForeground(Colors.white);
        name1.setFont(new Font("Futura", Font.PLAIN, 20));
        footer.add(name1, BorderLayout.WEST);

        JLabel name2 = new JLabel("Drissi El Haouari Wiam");
        name2.setForeground(Colors.white);
        name2.setHorizontalAlignment(0);
        name2.setFont(new Font("Futura", Font.PLAIN, 20));
        footer.add(name2, BorderLayout.CENTER);

        JLabel name3 = new JLabel("Ergouyeg Manar");
        name3.setForeground(Colors.white);
        name3.setFont(new Font("Futura", Font.PLAIN, 20));
        footer.add(name3, BorderLayout.EAST);
    }

    public void scrape() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                // stopping the user from pressing the save and scrape buttons while scraping
                scrapeButton.setEnabled(false);

                truncateTable("jobstemp");
                truncateTable("jobs");

                List<String> selectedItems = websitesList.getSelectedValuesList();
                for (String item: selectedItems) {
                    scrapedWebsiteLabel.setText(item);
                    progressBar.setValue(0);
                    logsLabel.setText("Scraping: 0%.");
                    if (item.equals("Rekrute.ma")) {
                        ScraperDebug.debugPrint("Scraping Rekrute.ma");
                        rekruteScraper.fetchPageNumber();
                        rekruteScraper.fetchPagesUrls();
                        rekruteScraper.fetchAllPostsUrl();
                        rekruteScraper.fetchAllPostsAttributes();
                        rekruteScraper.tempStoreAllPosts();
                    }
                    if (item.equals("Emploi.ma")) {
                        ScraperDebug.debugPrint("Scraping Emploi.ma");
                        emploiScraper.fetchPageNumber();
                        emploiScraper.fetchPagesUrls();
                        emploiScraper.fetchAllPostsUrl();
                        emploiScraper.fetchAllPostsAttributes();
                        emploiScraper.tempStoreAllPosts();
                    }
                    if (item.equals("M-job.ma")) {
                        ScraperDebug.debugPrint("Scraping M-job.ma");
                        mjobScraper.fetchPageNumber();
                        mjobScraper.fetchPagesUrls();
                        mjobScraper.fetchAllPostsUrl();
                        mjobScraper.fetchAllPostsAttributes();
                        mjobScraper.tempStoreAllPosts();
                    }
                }
                scrapeButton.setEnabled(true);
                new VisualizationUI();
                return null;
            }
        };
        sw.execute();

    }

    static class CenteredListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(4, 10, 4, 10));

            return label;
        }
    }

    private static void truncateTable(String tableName) {
        try (Connection connection = dbManager.makeConnection()) {
            String truncateQuery = "TRUNCATE TABLE " + tableName;
            try (PreparedStatement statement = connection.prepareStatement(truncateQuery)) {
                statement.executeUpdate();
                ScraperDebug.debugPrint("Table " + tableName + " truncated successfully.");
            }
        } catch (SQLException e) {
            ScraperDebug.debugPrint("Error truncating table " + tableName + ": " + e.getMessage());
        }
    }
}
