package com.fifzu.coronacalculator;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    static private JLabel jlCountries = new JLabel("Enter countries (seperated by ;):");
    static private JLabel jlPopulation = new JLabel("Enter population (in Mio): ");
    static private JTextField jtCountries = new JTextField(20);
    static private JTextField jtPopulation = new JTextField(20);
    static private JButton jbRun = new JButton("Run");

    public CoronaCalculator() {

        super("Corona Calculator");

        JPanel newPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(10, 10, 10, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;
        newPanel.add(jlCountries, constraints);

        constraints.gridx = 1;
        newPanel.add(jtCountries, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        newPanel.add(jlPopulation, constraints);

        constraints.gridx = 1;
        newPanel.add(jtPopulation, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;

        jtCountries.setText("Austria");
        jtPopulation.setText("8");
        jbRun.addActionListener(this);

        newPanel.add(jbRun, constraints);
        newPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameters"));
        add(newPanel);

        pack();
        setLocationRelativeTo(null);
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

    private static void startCalculation() {
        try {
            days = 0;
            countries = jtCountries.getText().split(";");
            population = Integer.parseInt(jtPopulation.getText() + "000000");

            dataReader = new DataReader(countries);

            records = dataReader.getRecords();

            if (records.size()>0) {
                double x = dataReader.getLastInfected();
                currentDate = dataReader.getLastDate();
                x = calculate(x);
                calculateDecrease(x);
                printData();
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
            records.add(dt+ ";"+(int) x + ";"+ (int) deaths);

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
            records.add(dt+ ";"+(int) x + ";"+ (int) deaths);

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
        if(ae.getSource() == this.jbRun){
            startCalculation();
    //        JOptionPane.showMessageDialog(null,"Finished Calculation","Corona Calculator", JOptionPane.INFORMATION_MESSAGE);
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
    private static void errorHandling (Exception e) {
        JOptionPane.showMessageDialog(null,e.toString(),"An error occurred!", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}
