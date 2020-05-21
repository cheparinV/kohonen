package practice.desktop;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator extends JPanel implements Runnable, ActionListener {

    private static final String[] datasetName = {
            "RUS", "USD", "EUR"
    };

    JPanel optionPane;
    JComponent canvas;
    private JButton startButton;
    private JComboBox<String> datasetBox;

    private JButton convertButton;
    private JTextField firstField;
    private JTextField secondField;

    private JComboBox<String> firstBox;
    private JComboBox<String> secondBox;


    public Calculator() {

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        //pane.setBorder(BorderFactory.createRaisedBevelBorder());

        JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        firstBox = new JComboBox<>();
        secondBox = new JComboBox<>();
        for (int i = 0; i < datasetName.length; i++) {
            firstBox.addItem(datasetName[i]);
            secondBox.addItem(datasetName[i]);
        }
        northPanel.add(firstBox);
        northPanel.add(secondBox);

        final JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        convertButton = new JButton("convert");
        convertButton.setActionCommand("convert");
        convertButton.addActionListener(this);
        southPanel.add(convertButton);

        firstField = new JTextField("0");
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.ipady = 60;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        firstField.setPreferredSize(new Dimension( 200, 24 ));
        //firstField.setColumns(45);
        secondField = new JTextField("0");
        GridBagConstraints second = new GridBagConstraints();
        second.fill = GridBagConstraints.HORIZONTAL;
        second.ipady = 60;
        second.weightx = 0.5;
        second.gridx = 1;
        second.gridy = 0;
        //secondField.setSize(300, 300);
        secondField.setPreferredSize(new Dimension( 200, 24 ));
        //secondField.setColumns(45);
        secondField.setEditable(false);

        pane.add(firstField, c);
        pane.add(secondField, second);
        setLayout(new BorderLayout());
        add(northPanel, BorderLayout.NORTH);
        add(southPanel, BorderLayout.SOUTH);
        add(pane, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {


    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        Calculator demo = new Calculator();
        JFrame f = new JFrame("Calculator");
        f.setSize(new Dimension(500, 500));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(demo);
        f.setVisible(true);
    }
}
