import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClassifierChoiceUI extends JFrame {

    public static void main(String[] args) { new ClassifierChoiceUI();}

    protected JComboBox<String> classifier;

    public ClassifierChoiceUI() {
        this.setResizable(false);
        this.setTitle("Job Portal Scraper");
        this.setBounds(550, 350, 300, 300);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout(0,0));

        /* header */
        JPanel header = new JPanel();
        header.setBackground(Colors.green);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        header.setLayout(new BorderLayout());
        this.getContentPane().add(header, BorderLayout.NORTH);

        JLabel title = new JLabel("Select A Classifier");
        title.setFont(new Font("Futura", Font.BOLD, 20));
        title.setHorizontalAlignment(0);
        title.setForeground(Colors.white);
        header.add(title, BorderLayout.CENTER);

        /* main section of the window */
        JPanel main = new JPanel();
        main.setBackground(Colors.white);
        main.setBorder(new EmptyBorder(30, 30, 30, 30));
        main.setLayout(new BorderLayout());
        this.getContentPane().add(main, BorderLayout.CENTER);

        classifier = new JComboBox<>();
        classifier.setModel(new DefaultComboBoxModel<>(new String[] {"zeroR", "j48", "FilteredClassifier"}));
        main.add(classifier, BorderLayout.CENTER);

        /*
        JPanel classificationContainer = new JPanel();
        classificationContainer.setBackground(Colors.white);
        // main.add(classificationContainer, BorderLayout.CENTER);
        classificationContainer.setLayout(null);
         */

        /* footer */
        JPanel footer = new JPanel();
        footer.setBackground(Colors.green);
        footer.setBorder(new EmptyBorder(10, 10, 10, 10));
        // footer.setLayout(new BorderLayout());
        this.getContentPane().add(footer, BorderLayout.SOUTH);

        JButton button = new JButton("Start Classification");
        button.setFont(new Font("Montseratt", Font.BOLD, 15));
        button.setForeground(Colors.green);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClassificationUI(classifier.getSelectedItem().toString());
            }
        });
        footer.add(button);

        this.setVisible(true);

    }
}
