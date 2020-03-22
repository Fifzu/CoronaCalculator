import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class CoronaCalculator {

    static final String[] COUNTRIES = {"Austria"};
    static final int POPULATION = 8000000;

    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    static int days;
    static double deaths;
    static double percent;
    static DataReader dataReader;
    static List<String> records;
    static String currentDate;

    public static void main(String[] args) throws IOException {

        dataReader = new DataReader(COUNTRIES);
        double x = dataReader.getLastInfected();
        records = dataReader.getRecords();
        currentDate = dataReader.getLastDate();

        x = calculate(x);
        calculateDecrease(x);
        printData();
    }

    private static double calculate(double x) throws IOException {

        if (x < POPULATION * 0.66) {
            x = x * 1.3;
            days++;
            percent = (x / POPULATION) * 100;
            deaths = x * 0.007;
            String dt  = getDate();

            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) percent + "%). "+ (int) deaths + " sind tot.");
            records.add(dt+ ";"+(int) x + ";"+ (int) deaths);
       //     fileWriter.write((int) x + ";" + (int) deaths + "\n");

            return calculate(x);
        } else {
            System.out.println("In " + days + " Tagen wurde eine Herdenimmunität erreicht.");
        }
        return x;
    }

    private static double calculateDecrease( double x) throws IOException {
        if (x > 1) {
            x = (x * 0.7);
            days++;
            percent = (x / POPULATION) * 100;
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

    private static void printData ()  {
        try {
            FileWriter fileWriter = new FileWriter(COUNTRIES[0] + ".csv");
            fileWriter.write("Datum;Infizierte;Tote" + "\n");
            for (int i=0;i<records.size();i++) {
                System.out.println(records.get(i));
                fileWriter.write(records.get(i)+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDate() {
        String dt  = currentDate;
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, days);  // number of days to add
        dt = sdf.format(c.getTime());
        return  dt;
    }
}
