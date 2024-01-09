import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.JTableHeader;
import java.sql.*;


public class VisualizationUI extends JFrame {
    protected JProgressBar progressBar;
    protected JLabel storingLabel;
    protected JButton classifierButton;
    protected JButton clusteringButton;
    protected JButton savetoDB;
    protected JButton cleanData;
    protected JButton chartButton;
    protected int numberOfPosts;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(VisualizationUI::new);
    }
    public VisualizationUI() {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setSize(1500, 800);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // change to dispose on close later on
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.brown);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel("Visualization");
        title.setFont(new Font("Futura", Font.BOLD, 50));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(5, 5, 5, 5));
        main.setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.CENTER);

        JPanel containerContainer = new JPanel(new BorderLayout(5, 5));
        containerContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
        containerContainer.setBackground(Colors.white);
        main.add(containerContainer, BorderLayout.CENTER);

        // Displaying scraped data
        JPanel container = new JPanel();
        container.setBackground(Colors.white);
        container.setBounds(5, 5, 700, 50);
        containerContainer.add(container, BorderLayout.NORTH);
        container.setLayout(new BorderLayout(5, 5));

        // put the table here
        JobsTable jobsTable = new JobsTable();
        jobsTable.setShowHorizontalLines(true);
        jobsTable.setGridColor(Colors.brown);
        jobsTable.setRowHeight(30);

        numberOfPosts = jobsTable.getRowCount();

        JTableHeader tableHeader = jobsTable.getTableHeader();
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 30));
        tableHeader.setUI(new BasicTableHeaderUI());
        tableHeader.setBackground(Colors.brown);
        tableHeader.setFont(new Font("Montseratt", Font.BOLD, 10));
        tableHeader.setForeground(Colors.white);

        JScrollPane scrollPane = new JScrollPane(jobsTable);
        scrollPane.setPreferredSize(new Dimension(700, 550));
        scrollPane.setBorder(new LineBorder(Colors.brown, 1));
        container.add(scrollPane, BorderLayout.NORTH);

        storingLabel = new JLabel("Storing: 0%");
        container.add(storingLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar();
        // progressBar.setStringPainted(true);
        container.add(progressBar, BorderLayout.SOUTH);

        // buttons
        JPanel buttonsContainer = new JPanel(new GridLayout(1, 5));
        buttonsContainer.setBackground(Colors.white);
        containerContainer.add(buttonsContainer, BorderLayout.CENTER);

        cleanData = new JButton("Clean Data");
        cleanData.setFont(new Font("Montseratt", Font.BOLD, 15));
        cleanData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // do something
            }
        });
        cleanData.setForeground(Colors.brown);
        buttonsContainer.add(cleanData);

        savetoDB = new JButton("Save to DataBase");
        savetoDB.setFont(new Font("Montseratt", Font.BOLD, 15));
        savetoDB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });
        savetoDB.setForeground(Colors.brown);
        buttonsContainer.add(savetoDB);

        chartButton = new JButton("Visualize Charts");
        chartButton.setFont(new Font("Montseratt", Font.BOLD, 15));
        chartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ChartUI();
            }
        });
        chartButton.setForeground(Colors.brown);
        buttonsContainer.add(chartButton);

        // machine learning buttons

        classifierButton = new JButton("Classification");
        classifierButton.setFont(new Font("Montseratt", Font.BOLD, 15));
        classifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClassifierChoiceUI();
            }
        });
        classifierButton.setForeground(Colors.brown);
        classifierButton.setEnabled(false);
        buttonsContainer.add(classifierButton);

        clusteringButton = new JButton("Clustering");
        clusteringButton.setFont(new Font("Montseratt", Font.BOLD, 15));
        clusteringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClusteringChoiceUI();
            }
        });
        clusteringButton.setForeground(Colors.brown);
        clusteringButton.setEnabled(false);
        buttonsContainer.add(clusteringButton);

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.brown);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    public void save() {
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                savetoDB.setEnabled(false);
                progressBar.setValue(0);
                storingLabel.setText("Saving to Database: starting...\n");
                storeAllPosts();
                classifierButton.setEnabled(true);
                clusteringButton.setEnabled(true);
                return null;
            }
        };
        sw.execute();

    }

    public void storeAllPosts() {
        DBManager dbManager = DBManager.getInstance();

        try {
            Connection connection = dbManager.makeConnection();

            String selectQuery = "SELECT * FROM jobstemp";
            PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
            ResultSet resultSet = selectStatement.executeQuery();

            String insertQuery = "INSERT INTO jobs VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            int count = 0;

            while (resultSet.next()) {
                insertStatement.setInt(1, resultSet.getInt("id"));
                insertStatement.setString(2, resultSet.getString("site_name"));
                insertStatement.setString(3, resultSet.getString("url"));
                insertStatement.setString(4, resultSet.getString("publish_date"));
                insertStatement.setString(5, resultSet.getString("apply_date"));
                insertStatement.setString(6, resultSet.getString("company_name"));
                insertStatement.setString(7, resultSet.getString("company_address"));
                insertStatement.setString(8, resultSet.getString("company_website"));
                insertStatement.setString(9, resultSet.getString("company_description"));
                insertStatement.setString(10, resultSet.getString("description"));
                insertStatement.setString(11, resultSet.getString("title"));
                insertStatement.setString(12, resultSet.getString("city"));
                insertStatement.setString(13, resultSet.getString("region"));
                insertStatement.setString(14, resultSet.getString("sector"));
                insertStatement.setString(15, resultSet.getString("job"));
                insertStatement.setString(16, resultSet.getString("contract_type"));
                insertStatement.setString(17, resultSet.getString("education_level"));
                insertStatement.setString(18, resultSet.getString("diploma"));
                insertStatement.setString(19, resultSet.getString("experience"));
                insertStatement.setString(20, resultSet.getString("profile_searched"));
                insertStatement.setString(21, resultSet.getString("personality_traits"));
                insertStatement.setString(22, resultSet.getString("hard_skills"));
                insertStatement.setString(23, resultSet.getString("soft_skills"));
                insertStatement.setString(24, resultSet.getString("recommended_skills"));
                insertStatement.setString(25, resultSet.getString("lang"));
                insertStatement.setString(26, resultSet.getString("lang_level"));
                insertStatement.setString(27, resultSet.getString("salary"));
                insertStatement.setString(28, resultSet.getString("social_advantages"));
                insertStatement.setString(29, resultSet.getString("remote"));

                insertStatement.executeUpdate();

                count++;
                int progress = (int) ((double) count / numberOfPosts * 100);
                System.out.println("numberOfPosts: "+numberOfPosts+", count: "+ count +", progress: ." + progress);
                updateProgressBar(progress);
            }

            JOptionPane.showMessageDialog(null, "Data successfully stored in Data Base.");
            storingLabel.setText("Data successfully stored in Data Base.");

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProgressBar(int value) {
        SwingUtilities.invokeLater(() -> {
            storingLabel.setText("Storing "+value+"%.");
            progressBar.setValue(value);
        });
    }

}