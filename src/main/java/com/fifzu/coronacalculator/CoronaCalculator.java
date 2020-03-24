package com.fifzu.coronacalculator;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.jfree.ui.RefineryUtilities;

public class CoronaCalculator extends JFrame implements ActionListener {

    static String[] countries;
    static int population;
    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    static int days;
    static double deaths;
    static double percent;
    static DataReader dataReader;
    static List<String> records;
    static String currentDate;

    static private JLabel lblCountries = new JLabel("Enter countries (seperated by ;):");
    static private JLabel lblPopulation = new JLabel("Enter population (in Mio): ");
    static private JTextField txtCountries = new JTextField(20);
    static private JTextField txtPopulation = new JTextField(20);
    static private JCheckBox cbxReadIn = new JCheckBox("Read in Data");
    static private JCheckBox cbcCalculate = new JCheckBox("Calculate Prediction");
    static private JCheckBox cbcPrint = new JCheckBox("Print to csv");
    static private JTextField txtTerminal;

    static private JButton jbRun = new JButton("Run");
    static private List<String> terminalText;


    public CoronaCalculator() {

        super("Corona Calculator");
        terminalText = new ArrayList<>();

        txtTerminal = new JTextField(20);
        txtTerminal.setEditable(false);


        setLayout(new BorderLayout());

        JPanel ParameterPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        ParameterPanel.add(lblCountries, constraints);

        constraints.gridx = 1;
        ParameterPanel.add(txtCountries, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        ParameterPanel.add(lblPopulation, constraints);

        constraints.gridx = 1;
        ParameterPanel.add(txtPopulation, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        ParameterPanel.add(cbxReadIn, constraints);

        constraints.gridx = 1;
        ParameterPanel.add(cbcCalculate, constraints);

        constraints.gridx = 0;
        constraints.gridy = 4;
        ParameterPanel.add(cbcPrint, constraints);

        constraints.gridx = 0;
        constraints.gridy = 5;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;

        txtCountries.setText("Austria");
        txtPopulation.setText("8");

        txtTerminal.setBackground(Color.BLACK);
        txtTerminal.setForeground(Color.WHITE);

        jbRun.addActionListener(this);

        ParameterPanel.add(jbRun, constraints);
        ParameterPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
        add(ParameterPanel,BorderLayout.NORTH);


        add(txtTerminal,BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);

        dataReader = new DataReader(this);
        try {
            dataReader.cloneRepo();
        } catch (Exception e) {
            errorHandling(e);
        }

    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CoronaCalculator().setVisible(true);
            }
        });
        } catch (Exception e) {
            errorHandling(e);
        }
    }

    private static void loadData() {
        try {
            records = dataReader.getRecords(countries);
        } catch (Exception e) {
            errorHandling(e);
        }
    }

    public static void writeToTerminal (String s) {
        txtTerminal.setText(s);
    }


    private static void startCalculation() {
        try {
            if (records.size()>0) {
                double x = dataReader.getLastInfected();
                currentDate = dataReader.getLastDate();
                x = calculate(x);
                calculateDecrease(x);

            } else {
                JOptionPane.showMessageDialog(null,"No data found!","An error occurred!", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            errorHandling(e);
        }
    }

    private static double calculate(double x) throws Exception {

        if (x < population * 0.66) {
            x = x * 1.3;
            days++;
            percent = (x / population) * 100;
            deaths = x * 0.007;
            String dt  = getDate();

            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) percent + "%). "+ (int) deaths + " sind tot.");
            records.add(dt+ ";"+(int) x + ";"+ (int) deaths+";true");

            return calculate(x);
        } else {
            System.out.println("In " + days + " Tagen wurde eine Herdenimmunität erreicht.");
        }
        return x;
    }

    private static double calculateDecrease( double x) throws Exception {
        if (x > 1) {
            x = (x * 0.7);
            days++;
            percent = (x / population) * 100;
            deaths = deaths + x * 0.007;
            String dt  = getDate();

            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) percent + "%). " + (int) deaths + " sind tot.");
            records.add(dt+ ";"+(int) x + ";"+ (int) deaths+";true");

            return calculateDecrease(x);
        } else {
            System.out.println("In " + days + " Tagen ist niemand mehr infiziert.");
        }
        return x;
    }

    private static void printData () throws Exception {

            File file = new File(System.getProperty("user.dir") + "\\datasheets");
            file.mkdir();
            file = new File(file + "\\" + countries[0] + ".csv");
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Datum;Infizierte;Tote" + "\n");
            for (int i=0;i<records.size();i++) {
                System.out.println(records.get(i));
                fileWriter.write(records.get(i)+"\n");
            }
            fileWriter.close();
            Runtime.getRuntime().exec("explorer.exe /select," +file);
    }

    public void actionPerformed (ActionEvent ae){
        try {
            days = 0;
            countries = txtCountries.getText().split(";");
            population = Integer.parseInt(txtPopulation.getText() + "000000");

            if(ae.getSource() == this.jbRun){
                if (cbxReadIn.isSelected()) {
                    loadData();
                }
                if (cbcCalculate.isSelected()) {
                    startCalculation();
                }
                if (cbcPrint.isSelected()) {
                    printData();
                }
                showPraph();
            }
            //        JOptionPane.showMessageDialog(null,"Finished Calculation","Corona Calculator", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            errorHandling(e);
        }
    }

    private static String getDate() throws Exception {
        String dt  = currentDate;
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(dt));
        c.add(Calendar.DATE, days);  // number of days to add
        dt = sdf.format(c.getTime());
        return  dt;
    }

    private void showPraph() throws ParseException {
        DataGraph graph = new DataGraph(countries[0], records);
        graph.pack();
        RefineryUtilities.centerFrameOnScreen(graph);
        graph.setVisible(true);
    }
    private static void errorHandling (Exception e) {
        JOptionPane.showMessageDialog(null,e.toString(),"An error occurred!", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
