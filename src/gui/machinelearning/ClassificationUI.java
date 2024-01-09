package gui.machinelearning;

import debug.ScraperDebug;
import gui.visualization.Colors;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClassificationUI extends JFrame {

    public static final JTextPane classificationPane = new JTextPane();

    public void j48(){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() -1);

            J48 j = new J48();
            j.buildClassifier(data);

            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(j, data, 10, new java.util.Random(1)); // 10-fold cross-validation

            evaluateModel(j,data);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    public void zeroR(){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() -1);

            ZeroR classifier = new ZeroR();
            classifier.buildClassifier(data);

            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(classifier, data, 10, new java.util.Random(1)); // 10-fold cross-validation

            evaluateModel(classifier,data);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }
    public void filteredClassifier(){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() -1);

            FilteredClassifier f = new FilteredClassifier();
            f.buildClassifier(data);


            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(f, data, 10, new java.util.Random(1)); // 10-fold cross-validation

            evaluateModel(f,data);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private static void evaluateModel(weka.classifiers.Classifier model, Instances testData) throws Exception {
        // Evaluate model
        Evaluation evaluation = new Evaluation(testData);
        evaluation.evaluateModel(model, testData);

        // Print results
        StringBuilder displayText = new StringBuilder();

        displayText.append("Model: " + model.getClass().getSimpleName() + "\n");
        displayText.append("Accuracy: " + evaluation.pctCorrect()+"\n");
        displayText.append("Precision: " + evaluation.weightedPrecision()+"\n");
        displayText.append("Recall: " + evaluation.weightedRecall()+"\n");
        displayText.append("F1 Score: " + evaluation.weightedFMeasure()+"\n");
        displayText.append("AUC-ROC: " + evaluation.areaUnderROC(0)+"\n");

        displayText.append(evaluation.toMatrixString()); //Confusion Matrix
        displayText.append(evaluation.toClassDetailsString()); //Detailed Accuracy By Class
        displayText.append(evaluation.toSummaryString()); //Summary
        displayText.append("=======================\n");
        ClassificationUI.classificationPane.setText(displayText.toString());
    }


    public ClassificationUI(String classifier) {
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

        JLabel title = new JLabel(classifier);
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

        classificationPane.setBackground(Colors.white);
        classificationPane.setForeground(Colors.brown);
        classificationPane.setText("Classification...");

        classificationContainer.add(classificationPane);
        scrollPane.setViewportView(classificationPane);
        classificationContainer.add(scrollPane);

        if (classifier.equals("zeroR")) {
            ScraperDebug.debugPrint("Classifier zeroR");
            zeroR();
        } else if (classifier.equals("j48")) {
            ScraperDebug.debugPrint("Classifier j48");
            j48();
        } else {
            ScraperDebug.debugPrint("Classifier FilteredClassifier");
            filteredClassifier();
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
