package scrapers;

import db.DBManager;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertTrue;
import static org.jsoup.helper.Validate.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class MjobScraperTest {

    public static MjobScraper scraper = new MjobScraper();

    protected static int total = 1;

    public static void main(String[] args) {
        MjobScraperTest test = new MjobScraperTest();
        scraper.setListener(new ScraperListener() {
            @Override
            public void updateTotalPages(int pages) {
                // logsLabel.setText("Number of pages found: " + pages);
                System.out.println("Number of pages found: " + pages);
            }

            @Override
            public void updateTotalPosts(int posts) {
                // logsLabel.setText("Number of posts found: " + posts);
                System.out.println("Number of posts found: " + posts);
                total = posts;
            }

            @Override
            public void updateCurrentPost(int post, String url) {
                int progress = (post * 100) / total;
                System.out.println("Scraping "+progress+"% : " + url);
            }

            @Override
            public void updateCurrentStorage(int current) {
                int progress = (current * 100) / total;
                System.out.println("Storing "+progress+"%.");
            }

            @Override
            public void finishedMysqlStorage(int success, int failed) {
                // JOptionPane.showMessageDialog(null, "Finished storing: "+success+" success, "+failed+" failed.");
            }
        });

        /*System.out.println("Running fetchPageNumber test...");
        test.fetchPageNumber();

        System.out.println("Running fetchPagesUrls test...");
        test.fetchPagesUrls();

        System.out.println("Running fetchAllPostsUrl test...");
        test.fetchAllPostsUrl();

        System.out.println("Running fetchAllPostsAttributes test...");
        test.fetchAllPostsAttributes();*/

        System.out.println("Running storeAllPosts test...");
        test.storeAllPosts();

        System.out.println("Running tempStoreAllPosts test...");
        test.tempStoreAllPosts();

        System.out.println("All tests passed successfully!");
    }

    @Test
    public void fetchPageNumber() {
        scraper.fetchPageNumber();
        assertTrue(scraper.getPagesNumber() > 0);
    }

    @Test
    public void fetchPagesUrls() {
        scraper.fetchPagesUrls();
        assertNotNull(scraper.getPagesUrl());
        assertTrue(scraper.getPagesUrl().size() > 0);
    }

    @Test
    public void fetchAllPostsUrl() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        assertNotNull(scraper.getPostsUrl());
        assertTrue(scraper.getPostsUrl().size() > 0);
    }

    @Test
    public void fetchAllPostsAttributes() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        scraper.fetchAllPostsAttributes();
        assertNotNull(scraper.getPosts());
        assertTrue(scraper.getPosts().size() > 0);
    }

    @Test
    public void storeAllPosts() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        scraper.fetchAllPostsAttributes();
        scraper.storeAllPosts();

        try (Connection conn = DBManager.getInstance().makeConnection()) {

            String query = "SELECT COUNT(*) FROM jobs";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    int actualRowCount = resultSet.getInt(1);

                    int expectedRowCount = scraper.getPosts().size();
                    assertEquals(expectedRowCount, actualRowCount);
                } else {
                    fail("Failed to retrieve row count from the database");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            // e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }

    @Test
    public void tempStoreAllPosts() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        scraper.fetchAllPostsAttributes();
        scraper.tempStoreAllPosts();

        try (Connection conn = DBManager.getInstance().makeConnection()) {

            String query = "SELECT COUNT(*) FROM jobstemp";
            try (Statement statement = conn.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                if (resultSet.next()) {
                    int actualRowCount = resultSet.getInt(1);

                    int expectedRowCount = scraper.getPosts().size();
                    assertEquals(expectedRowCount, actualRowCount);
                } else {
                    fail("Failed to retrieve row count from the database");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }
}
