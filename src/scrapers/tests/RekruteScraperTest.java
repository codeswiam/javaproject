package scrapers.tests;

import db.DBManager;
import org.junit.Test;
import scrapers.RekruteScraper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static junit.framework.TestCase.assertTrue;
import static org.jsoup.helper.Validate.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class RekruteScraperTest {

    public RekruteScraper scraper = new RekruteScraper();

    @Test
    public void fetchPageNumber() {
        scraper.fetchPageNumber();
        assertTrue(scraper.getPagesNumber() > 0);
    }

    @Test
    public void fetchPagesUrls() {
        scraper.fetchPageNumber();
        scraper.fetchPagesUrls();
        assertNotNull(scraper.getPagesUrl());
        assertTrue(scraper.getPagesUrl().size() > 0);
    }

    @Test
    public void fetchAllPostsUrl() {
        scraper.fetchPageNumber();
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        assertNotNull(scraper.getPostsUrl());
        assertTrue(scraper.getPostsUrl().size() > 0);
    }

    @Test
    public void fetchAllPostsAttributes() {
        scraper.fetchPageNumber();
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        scraper.fetchAllPostsAttributes();
        assertNotNull(scraper.getPosts());
        assertTrue(scraper.getPosts().size() > 0);
    }

    @Test
    public void storeAllPosts() {
        scraper.fetchPageNumber();
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
                    assertEquals(expectedRowCount-1, actualRowCount);
                } else {
                    fail("Failed to retrieve row count from the database");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }

    @Test
    public void tempStoreAllPosts() {
        scraper.fetchPageNumber();
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
                    assertEquals(expectedRowCount-1, actualRowCount);
                } else {
                    fail("Failed to retrieve row count from the database");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }
}
