package scrapers;

import db.DBManager;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.jsoup.helper.Validate.fail;
import static org.junit.jupiter.api.Assertions.*;


class EmploiScraperTest {

    private EmploiScraper scraper = new EmploiScraper();

    @Test
    void fetchPageNumber() {
        scraper.fetchPageNumber();
        assertTrue(scraper.getPagesNumber() > 0);
    }

    @Test
    void fetchPagesUrls() {
        scraper.fetchPagesUrls();
        assertNotNull(scraper.getPagesUrl());
        assertTrue(scraper.getPagesUrl().size() > 0);
    }

    @Test
    void fetchAllPostsUrl() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        assertNotNull(scraper.getPostsUrl());
        assertTrue(scraper.getPostsUrl().size() > 0);
    }

    @Test
    void fetchAllPostsAttributes() {
        scraper.fetchPagesUrls();
        scraper.fetchAllPostsUrl();
        scraper.fetchAllPostsAttributes();
        assertNotNull(scraper.getPosts());
        assertTrue(scraper.getPosts().size() > 0);
    }

    @Test
    void storeAllPosts() {
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
    void tempStoreAllPosts() {
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
           // e.printStackTrace();
            fail("Failed to connect to the database");
        }
    }
}
