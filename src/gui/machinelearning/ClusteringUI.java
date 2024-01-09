package gui.machinelearning;

import debug.ScraperDebug;
import gui.visualization.Colors;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClusteringUI extends JFrame {
    public static final JTextPane clusteringPane = new JTextPane();

    public void makeDensityBasedClusterer() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
        Instances data = source.getDataSet();

        // Remove the class attribute before clustering
        data = removeClassAttribute(data);

        MakeDensityBasedClusterer m = new MakeDensityBasedClusterer();
        m.buildClusterer(data);
        evaluateModel(m, data);
    }
    public void cobweb() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
        Instances data = source.getDataSet();

        // Remove the class attribute before clustering
        data = removeClassAttribute(data);

        Cobweb c = new Cobweb();
        c.buildClusterer(data);
        evaluateModel(c, data);
    }
    public static Instances removeClassAttribute(Instances data) {
        // Remove the class attribute
        if (data.classIndex() != -1) {
            Instances newData = new Instances(data);
            newData.deleteAttributeAt(data.classIndex());
            return newData;
        }
        return data;
    }

    public static void evaluateModel(MakeDensityBasedClusterer clusterer, Instances data) {
        try {
            // Create ClusterEvaluation instance
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(clusterer);

            // Evaluate the clusterer
            eval.evaluateClusterer(data);

            // Print evaluation results

            StringBuilder displayText = new StringBuilder();

            displayText.append("=== Cluster Evaluation ===\n");
            displayText.append("Model: " + clusterer.getClass().getSimpleName()+ "\n");
            displayText.append("Number of clusters: " + eval.getNumClusters()+ "\n");
            displayText.append("Log-likelihood: " + eval.getLogLikelihood()+ "\n");

            // Print detailed results
            displayText.append("\n=== Cluster Results ===\n");
            displayText.append(eval.clusterResultsToString());

            clusteringPane.setText(displayText.toString());
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    public static void evaluateModel(Cobweb clusterer, Instances data) {
        try {
            // Create ClusterEvaluation instance
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(clusterer);

            // Evaluate the clusterer
            eval.evaluateClusterer(data);

            StringBuilder displayText = new StringBuilder();

            // Print evaluation results
            displayText.append("=== Cluster Evaluation ===\n");
            displayText.append("Model: " + clusterer.getClass().getSimpleName()+ "\n");
            displayText.append("Number of clusters: " + eval.getNumClusters()+ "\n");
            displayText.append("Log-likelihood: " + eval.getLogLikelihood()+ "\n");

            // Print detailed results
            displayText.append("\n=== Cluster Results ===\n");
            displayText.append(eval.clusterResultsToString()+ "\n");

            clusteringPane.setText(displayText.toString());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }


    public ClusteringUI(String clusterer) throws Exception {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setBounds(550, 350, 800, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.green);
        header.setBorder(new EmptyBorder(20, 20, 20, 20));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel(clusterer);
        title.setFont(new Font("Futura", Font.BOLD, 50));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));
        main.setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.CENTER);


        JPanel clusteringContainer = new JPanel();
        clusteringContainer.setBackground(Colors.white);
        main.add(clusteringContainer, BorderLayout.CENTER);
        clusteringContainer.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Colors.white);
        scrollPane.setForeground(Colors.brown);
        scrollPane.setBounds(30, 20, 700, 250);

        clusteringPane.setBackground(Colors.white);
        clusteringPane.setForeground(Colors.brown);
        clusteringPane.setText("clustering...");

        clusteringContainer.add(clusteringPane);
        scrollPane.setViewportView(clusteringPane);
        clusteringContainer.add(scrollPane);

        if (clusterer.equals("DensityBasedClusterer")) {
            ScraperDebug.debugPrint("Density Based Clusterer");
            makeDensityBasedClusterer();
        } else if (clusterer.equals("Cobweb")) {
            ScraperDebug.debugPrint("Cobweb");
            cobweb();
        }

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.green);
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}
