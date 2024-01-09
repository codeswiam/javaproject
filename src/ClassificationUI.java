import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import weka.classifiers.Evaluation;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.rules.ZeroR;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class ClassificationUI extends JFrame {

    public static JTextPane classificationPane;

    public void j48(){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() -1);

            J48 j = new J48();
            j.buildClassifier(data);


            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(j, data, 10, new java.util.Random(1)); // 10-fold cross-validation

            //System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            //System.out.println(eval.toClassDetailsString());
            //System.out.println(eval.toMatrixString());

            evaluateModel(j,data);

        } catch (Exception e) {
            e.printStackTrace();
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

            //System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            //System.out.println(eval.toClassDetailsString());
            //System.out.println(eval.toMatrixString());

            evaluateModel(classifier,data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void FilteredClassifier(){
        try {
            ConverterUtils.DataSource source = new ConverterUtils.DataSource("data.arff");
            Instances data = source.getDataSet();

            data.setClassIndex(data.numAttributes() -1);

            FilteredClassifier F = new FilteredClassifier();
            F.buildClassifier(data);


            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(F, data, 10, new java.util.Random(1)); // 10-fold cross-validation

            //System.out.println(eval.toSummaryString("\nResults\n======\n", false));
            //System.out.println(eval.toClassDetailsString());
            //System.out.println(eval.toMatrixString());
            evaluateModel(F,data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void evaluateModel(weka.classifiers.Classifier model, Instances testData) throws Exception {
        // Evaluate model
        Evaluation evaluation = new Evaluation(testData);
        evaluation.evaluateModel(model, testData);
        System.out.println("Model: " + model.getClass().getSimpleName());
        System.out.println("Accuracy: " + evaluation.pctCorrect());
        System.out.println("Precision: " + evaluation.weightedPrecision());
        System.out.println("Recall: " + evaluation.weightedRecall());
        System.out.println("F1 Score: " + evaluation.weightedFMeasure());
        System.out.println("AUC-ROC: " + evaluation.areaUnderROC(0));

        System.out.println(evaluation.toMatrixString()); //Confusion Matrix
        System.out.println( evaluation.toClassDetailsString()); //Detailed Accuracy By Class
        System.out.println(evaluation.toSummaryString()); //Summary
        System.out.println("=======================\n");
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

        classificationPane = new JTextPane();
        classificationPane.setBackground(Colors.white);
        classificationPane.setForeground(Colors.brown);
        classificationPane.setText("Classification...");

        classificationContainer.add(classificationPane);
        scrollPane.setViewportView(classificationPane);
        classificationContainer.add(scrollPane);

        /*
         * to display classification info in the text pane classificationPane
         * use ClassificationUI.classificationPane.setText("string to be displayed");
         * */

        if (classifier.equals("zeroR")) {
            System.out.println("Classifier zeroR");
            zeroR();
        } else if (classifier.equals("j48")) {
            System.out.println("Classifier j48");
            j48();
        } else {
            System.out.println("Classifier FilteredClassifier");
            FilteredClassifier();
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
