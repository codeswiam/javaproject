package gui.visualization;

import db.DBManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JobsTable extends JTable {
    private DBManager dbManager = DBManager.getInstance();

    public JobsTable() {
        // dbManager = db.DBManager.getInstance();
        DefaultTableModel tableModel = new DefaultTableModel();
        setModel(tableModel);

        // Load data
        loadData(tableModel);
    }

    private void loadData(DefaultTableModel tableModel) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = dbManager.makeConnection();

            statement = connection.createStatement();

            String query = "SELECT * FROM jobstemp";
            resultSet = statement.executeQuery(query);

            // Get column count
            int columnCount = resultSet.getMetaData().getColumnCount();

            // Add column headers to the table model
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(resultSet.getMetaData().getColumnName(i));
            }

            // Add rows to the table model
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
           // e.printStackTrace();
        } finally {
            // Close JDBC resources
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
              //  e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Jobs Table Example");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JobsTable jobsTable = new JobsTable();
            JScrollPane scrollPane = new JScrollPane(jobsTable);

            frame.add(scrollPane);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}