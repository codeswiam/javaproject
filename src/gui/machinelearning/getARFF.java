package gui.machinelearning;

import debug.ScraperDebug;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class getARFF {
    public static void convert(String sourcePath, String destPath) throws Exception {
        // Load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(sourcePath));
        Instances dataSet = loader.getDataSet();
        //6,11,12,14,16
        // Specify the columns you want to keep (e.g., 1,3,5 for columns 1, 3, and 5)
        String indicesToRemove = "1,2,3,4,5,7,8,9,10,11,13,14,15,17,18,19,20,21,22,23,24,25,26,27,28"; // Replace with the indices of columns you want to remove
        Remove removeFilter = new Remove();
        removeFilter.setAttributeIndices(indicesToRemove);
        removeFilter.setInputFormat(dataSet);
        Instances filteredData = Filter.useFilter(dataSet, removeFilter);

        // Save ARFF
        BufferedWriter writer = new BufferedWriter(new FileWriter(destPath));
        writer.write(filteredData.toString());
        writer.flush();
        writer.close();
    }
    public static void convert2(String sourcePath, String destPath) throws Exception {
            // Load CSV with specific columns
            CSVLoader loader = new CSVLoader();

            // Specify the columns you want to include (e.g., 1,3,5 for columns 1, 3, and 5)
            String[] options = {"-0", ","};
            loader.setOptions(options);
            loader.setSource(new File(sourcePath));
            Instances dataSet = loader.getDataSet();

            // Save ARFF
            BufferedWriter writer = new BufferedWriter(new FileWriter(destPath));
            writer.write(dataSet.toString());
            writer.flush();
            writer.close();

    }
    public static void convert3(String sourcePath, String destPath) throws Exception {
        // Load CSV
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(sourcePath));
        Instances dataSet = loader.getDataSet();

        // Specify the indices of columns to remove (e.g., 2, 4 for columns 2 and 4)
        String indicesToRemove = "2,4"; // Replace with the indices of columns you want to remove
        Remove removeFilter = new Remove();
        removeFilter.setAttributeIndices(indicesToRemove);
        removeFilter.setInputFormat(dataSet);
        Instances filteredData = Filter.useFilter(dataSet, removeFilter);

        // Save ARFF
        BufferedWriter writer = new BufferedWriter(new FileWriter(destPath));
        writer.write(filteredData.toString());
        writer.flush();
        writer.close();

        // Perform clustering (example with SimpleKMeans)
        SimpleKMeans kMeans = new SimpleKMeans();
        kMeans.setNumClusters(3); // Set the desired number of clusters
        kMeans.buildClusterer(filteredData);

        // Get cluster assignments for each instance
        int[] assignments = kMeans.getAssignments();

        // Print cluster assignments for each instance
        for (int i = 0; i < filteredData.numInstances(); i++) {
            ScraperDebug.debugPrint("Instance " + i + " belongs to cluster " + assignments[i]);
        }
    }

    public static void main(String[] args) throws Exception {
        convert2("temp.csv", "temp.arff");
    }
}


