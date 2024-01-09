import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChartUI extends JFrame {

    private DBManager dbManager;

    public ChartUI() {
        dbManager = DBManager.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setSize(1200, 1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // change to dispose later on
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.brown);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel("Charts");
        title.setFont(new Font("Futura", Font.BOLD, 50));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.setLayout(new GridLayout(2, 2));
        this.getContentPane().add(main, BorderLayout.CENTER);

        JFreeChart cityChart = createCityChart();
        cityChart.setBackgroundPaint(Colors.white);

        ChartPanel cityPanel = new ChartPanel(cityChart);
        cityPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        cityPanel.setBackground(Colors.white);
        main.add(cityPanel);

        JFreeChart contractChart = createContractBarChart();
        contractChart.setBackgroundPaint(Colors.white);

        ChartPanel contractPanel = new ChartPanel(contractChart);
        contractPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        contractPanel.setBackground(Colors.white);
        main.add(contractPanel);

        JFreeChart educationChart = createEducationRadarChart();
        educationChart.setBackgroundPaint(Colors.white);

        ChartPanel educationPanel = new ChartPanel(educationChart);
        educationPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        educationPanel.setBackground(Colors.white);
        main.add(educationPanel);

        JFreeChart languageChart = createLanguageDonutChart();
        languageChart.setBackgroundPaint(Colors.white);

        ChartPanel languagePanel = new ChartPanel(languageChart);
        languagePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        languagePanel.setBackground(Colors.white);
        main.add(languagePanel);

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.brown);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        this.setVisible(true);
    }

    private JFreeChart createCityChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection connection = dbManager.makeConnection()) {
            String query = "SELECT city, COUNT(*) AS post_count FROM jobstemp GROUP BY city";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String city = resultSet.getString("city");
                    int postCount = resultSet.getInt("post_count");
                    dataset.setValue(city, postCount);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        JFreeChart chart = ChartFactory.createPieChart(
                "Jop Posts Per City",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();

        int numberOfItems = dataset.getItemCount();
        System.out.println(numberOfItems);

        int n = 0;
        while (n <= numberOfItems) {
            plot.setSectionPaint(n, new Color(156, 102, 68));
            plot.setSectionPaint(n + 1, new Color(127, 85, 57));
            plot.setSectionPaint(n + 2, new Color(176, 137, 104));
            plot.setSectionPaint(n + 3, new Color(221, 184, 146));
            plot.setSectionPaint(n + 4, new Color(230, 204, 178));
            n += 5;
        }

        return chart;
    }

    private JFreeChart createContractBarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection connection = dbManager.makeConnection()) {
            String query = "SELECT contract_type, COUNT(*) AS post_count FROM jobstemp GROUP BY contract_type";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String contractType = resultSet.getString("contract_type");
                    int postCount = resultSet.getInt("post_count");
                    dataset.addValue(postCount, "Contract Types", contractType);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Contract Types per Job Post",
                "Contract Type",
                "Number of Posts",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = new BarRenderer();
        renderer.setItemMargin(0.1);
        plot.setRenderer(renderer);
        plot.getRenderer().setPaint(new Color(127, 85, 57));

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        domainAxis.setMaximumCategoryLabelWidthRatio(0.8f);
        domainAxis.setMaximumCategoryLabelLines(2);

        return chart;
    }

    private JFreeChart createEducationRadarChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        try (Connection connection = dbManager.makeConnection()) {
            String query = "SELECT education_level, COUNT(*) AS post_count FROM jobstemp GROUP BY education_level";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String educationLevel = resultSet.getString("education_level");
                    int postCount = resultSet.getInt("post_count");
                    dataset.addValue(postCount, "Count", educationLevel);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        SpiderWebPlot plot = new SpiderWebPlot(dataset);
        plot.setStartAngle(90);
        plot.setInteriorGap(0.4);
        plot.setSeriesPaint(new Color(127, 85, 57));

        JFreeChart chart = new JFreeChart(
                "Education Levels per Job Post",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                false);
        chart.setBackgroundPaint(Color.white);

        return chart;
    }

    private JFreeChart createLanguageDonutChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        try (Connection connection = dbManager.makeConnection()) {
            String query = "SELECT lang, COUNT(*) AS post_count FROM jobstemp GROUP BY lang";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    String language = resultSet.getString("lang");
                    int postCount = resultSet.getInt("post_count");
                    dataset.setValue(language, postCount);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RingPlot plot = new RingPlot(dataset);
        plot.setSeparatorsVisible(true); // Show separators between sections
        plot.setSeparatorStroke(new BasicStroke(0.5f)); // Customize separator stroke
        plot.setSeparatorPaint(Color.WHITE); // Customize separator color

        int numberOfItems = dataset.getItemCount();
        System.out.println(numberOfItems);

        int n = 0;
        while (n <= numberOfItems) {
            plot.setSectionPaint(n, new Color(156, 102, 68));
            plot.setSectionPaint(n + 1, new Color(127, 85, 57));
            plot.setSectionPaint(n + 2, new Color(176, 137, 104));
            plot.setSectionPaint(n + 3, new Color(221, 184, 146));
            plot.setSectionPaint(n + 4, new Color(230, 204, 178));
            n += 5;
        }

        JFreeChart chart = new JFreeChart(
                "Languages per Job Post",
                JFreeChart.DEFAULT_TITLE_FONT,
                plot,
                false
        );

        return chart;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChartUI example = new ChartUI();
        });
    }
}
