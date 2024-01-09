import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Cobweb;
import weka.clusterers.MakeDensityBasedClusterer;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ClusteringUI extends JFrame {
    public static JTextPane clusteringPane;

    public void MakeDensityBasedClusterer() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
        Instances data = source.getDataSet();

        // Remove the class attribute before clustering
        data = removeClassAttribute(data);

        MakeDensityBasedClusterer m = new MakeDensityBasedClusterer();
        m.buildClusterer(data);
        evaluateModel(m, data);
    }
    public void Cobweb() throws Exception {
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
            System.out.println("=== Cluster Evaluation ===");
            System.out.println("Model: " + clusterer.getClass().getSimpleName());
            System.out.println("Number of clusters: " + eval.getNumClusters());
            System.out.println("Log-likelihood: " + eval.getLogLikelihood());

            // Print detailed results
            System.out.println("\n=== Cluster Results ===");
            System.out.println(eval.clusterResultsToString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void evaluateModel(Cobweb clusterer, Instances data) {
        try {
            // Create ClusterEvaluation instance
            ClusterEvaluation eval = new ClusterEvaluation();
            eval.setClusterer(clusterer);

            // Evaluate the clusterer
            eval.evaluateClusterer(data);

            // Print evaluation results
            System.out.println("=== Cluster Evaluation ===");
            System.out.println("Model: " + clusterer.getClass().getSimpleName());
            System.out.println("Number of clusters: " + eval.getNumClusters());
            System.out.println("Log-likelihood: " + eval.getLogLikelihood());

            // Print detailed results
            System.out.println("\n=== Cluster Results ===");
            System.out.println(eval.clusterResultsToString());
        } catch (Exception e) {
            e.printStackTrace();
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


        JPanel classificationContainer = new JPanel();
        classificationContainer.setBackground(Colors.white);
        main.add(classificationContainer, BorderLayout.CENTER);
        classificationContainer.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Colors.white);
        scrollPane.setForeground(Colors.brown);
        scrollPane.setBounds(30, 20, 700, 250);

        clusteringPane = new JTextPane();
        clusteringPane.setBackground(Colors.white);
        clusteringPane.setForeground(Colors.brown);
        clusteringPane.setText("Classification...");

        classificationContainer.add(clusteringPane);
        scrollPane.setViewportView(clusteringPane);
        classificationContainer.add(scrollPane);

        /*
         * to display classification info in the text pane classificationPane
         * use ClassificationUI.classificationPane.setText("string to be displayed");
         * */

        if (clusterer.equals("DensityBasedClusterer")) {
            System.out.println("Density Based Clusterer");
            MakeDensityBasedClusterer();
        } else if (clusterer.equals("Cobweb")) {
            System.out.println("Cobweb");
            Cobweb();
        } /*else {
            System.out.println("Classifier FilteredClassifier");
            FilteredClassifier();
        }*/

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.green);
        footer.setBorder(new EmptyBorder(20, 20, 20, 20));
        footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        this.setVisible(true);
    }
}
