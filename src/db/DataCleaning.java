package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataCleaning {
    public DBManager dbManager = DBManager.getInstance();

    public static void main(String[] args) {
        handleSalaryMissingValues();
    }

    public static void handleSalaryMissingValues() {
        try {

            DBManager dbManager = DBManager.getInstance();
            Connection connection = dbManager.makeConnection();
            Statement statement = connection.createStatement();

            // Patterns to match salary values
            String rangePattern = "de\\s*(\\d+)\\s*dh\\s*à\\s*(\\d+)\\s*dh";
            String plusPattern = "\\+\\s*de\\s*(\\d+)\\s*000\\s*dh";
            String BigPattern = "de\\s*(\\d+)\\s*000\\s*dh\\s*à\\s*(\\d+)\\s*000\\s*dh";

            // Compile the patterns
            Pattern rangePatternCompiled = Pattern.compile(rangePattern);
            Pattern plusPatternCompiled = Pattern.compile(plusPattern);
            Pattern bigPatternCompiled = Pattern.compile(BigPattern);

            // Step 1: Replace values written as "de X à Y" with the mean of X and Y
            String rangeQuery = "SELECT id, salary FROM jobstemp WHERE (salary LIKE 'de%à%dh' OR salary LIKE '+ de%') AND salary NOT IN ('a négocier', 'non défini')";
            ResultSet rangeResult = statement.executeQuery(rangeQuery);

            // Lists to store IDs and means separately
            List<Integer> ids = new ArrayList<>();
            List<Integer> means = new ArrayList<>();

            while (rangeResult.next()) {
                int id = rangeResult.getInt("id");
                String originalSalary = rangeResult.getString("salary");

                Matcher rangeMatcher = rangePatternCompiled.matcher(originalSalary);
                Matcher plusMatcher = plusPatternCompiled.matcher(originalSalary);
                Matcher bigMatcher = bigPatternCompiled.matcher(originalSalary);

                if (rangeMatcher.matches()) {
                    int lowerBound = Integer.parseInt(rangeMatcher.group(1));
                    int upperBound = Integer.parseInt(rangeMatcher.group(2));
                    int mean = (lowerBound + upperBound) / 2;

                    // Save the ID and mean separately for later updates
                    ids.add(id);
                    means.add(mean);

                    System.out.println("Updated salary for ID " + id);
                }
                else if (plusMatcher.matches()) {
                    int lowerBound = Integer.parseInt(plusMatcher.group(1));
                    int mean = lowerBound * 1000;
                    // Save the ID and mean separately for later updates
                    ids.add(id);
                    means.add(mean);
                    System.out.println("Updated salary for ID " + id);
                }
                else if (bigMatcher.matches()) {
                    int lowerBound = Integer.parseInt(bigMatcher.group(1));
                    int upperBound = Integer.parseInt(bigMatcher.group(2));
                    lowerBound= lowerBound * 1000;
                    upperBound= upperBound * 1000;
                    int mean = (lowerBound + upperBound) / 2;

                    // Save the ID and mean separately for later updates
                    ids.add(id);
                    means.add(mean);

                    System.out.println("Updated salary for ID " + id);
                }
            }

            // Close the first result set
            rangeResult.close();

            // Update the database with the calculated means
            for (int i = 0; i < ids.size(); i++) {
                int id = ids.get(i);
                int mean = means.get(i);

                String updateQuery = "UPDATE jobstemp SET salary = " + mean + " WHERE id = " + id;
                statement.executeUpdate(updateQuery);
            }

            // Step 2: Replace values "a négocier" and "non défini" with empty values
            String replaceNegotiateQuery = "UPDATE jobstemp SET salary = NULL WHERE salary IN ('a négocier', 'non défini')";
            statement.executeUpdate(replaceNegotiateQuery);
            System.out.println("Replaced 'a négocier' and 'non défini' with empty values");

            // Step 3: Replace remaining missing values with the mean of all salary values
            String overallMeanQuery = "SELECT AVG(CAST(salary AS SIGNED)) AS meanSalary FROM jobstemp WHERE salary IS NOT NULL";
            ResultSet overallMeanResult = statement.executeQuery(overallMeanQuery);

            if (overallMeanResult.next()) {
                double overallMeanSalary = overallMeanResult.getDouble("meanSalary");

                // Update missing values with the overall mean
                String updateMissingQuery = "UPDATE jobstemp SET salary = " + overallMeanSalary + " WHERE salary IS NULL";
                statement.executeUpdate(updateMissingQuery);

                System.out.println("Updated missing values with the mean salary");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void cleaningEmptyRows() {
        try {
            DBManager dbManager = DBManager.getInstance();
            Connection connection = dbManager.makeConnection();
            Statement statement = connection.createStatement();
            // Set the threshold for the number of allowed missing values in a row
            int maxMissingValues = 4; // Adjust as needed

            // Identify and remove rows with more than maxMissingValues missing values
            String selectQuery = "SELECT * FROM jobstemp";
            ResultSet resultSet = statement.executeQuery(selectQuery);

            ResultSetMetaData metaData = resultSet.getMetaData();
            int numColumns = metaData.getColumnCount();

            // List to store IDs of rows to be deleted
            List<Integer> idsToDelete = new ArrayList<>();

            while (resultSet.next()) {
                int missingValues = 0;

                for (int i = 1; i <= numColumns; i++) {
                    if (resultSet.getString(i) == null || resultSet.getString(i).isEmpty()) {
                        missingValues++;
                    }
                }

                if (missingValues > maxMissingValues) {
                    // Add the ID to the list for later deletion
                    int idToDelete = resultSet.getInt("id");  // Replace "id" with your primary key column name
                    idsToDelete.add(idToDelete);
                    System.out.println("Marked row with ID " + idToDelete + " for deletion");
                }
            }

            // Close the result set before performing the delete operations
            resultSet.close();

            // Delete the marked rows
            for (int id : idsToDelete) {
                String deleteQuery = "DELETE FROM jobstemp WHERE id = " + id;
                statement.executeUpdate(deleteQuery);
                System.out.println("Deleted row with ID " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
  

