package gui.machinelearning;

import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.clusterers.DBSCAN;

import java.io.File;

public class kmean {
    public static void main(String[] args) {
        try {
            // Load ARFF file
            ArffLoader loader = new ArffLoader();
            loader.setFile(new File("temp.arff"));
            Instances data = loader.getDataSet();

            // Apply DBSCAN
            DBSCAN dbscan = new DBSCAN();
            dbscan.setOptions(new String[]{"-E", "0.9", "-M", "6"}); // Set parameters as needed
            dbscan.buildClusterer(data);

            // Print cluster assignments for each instance
            for (int i = 0; i < data.numInstances(); i++) {
                int cluster = dbscan.clusterInstance(data.instance(i));
                System.out.println("Instance " + i + " belongs to cluster " + cluster);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
