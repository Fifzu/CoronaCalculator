import java.io.FileWriter;
import java.io.IOException;

public class CoronaCalculator {
    static int days;
    static double deaths;
    static int population = 1360000000;
    static double percent;
    static FileWriter fileWriter;
    static DataReader dataReader;

    public static void main(String[] args) throws IOException {

        String[] country = {"South Korea", "Korea, South", "Republic of Korea"};
        dataReader = new DataReader(country);


        fileWriter = new FileWriter("corona.csv");

        try {
            fileWriter.write("Infizierte;Tote" + "\n");
            double x = 287300;
            x = calculate(x);
            calculateDecrease(x);
            fileWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    private static double calculate(double x) throws IOException {

        if (x < population * 0.66) {
            x = x * 1.3;
            days++;
            percent = (x / population) * 100;
            deaths = x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) percent + "%). "+ (int) deaths + " sind tot.");

            fileWriter.write((int) x + ";" + (int) deaths + "\n");

            return calculate(x);
        } else {
            System.out.println("In " + days + " Tagen wurde eine Herdenimmunität erreicht.");

        }
        return x;
    }

    private static double calculateDecrease( double x) throws IOException {
        if (x > 10) {
            x = (x * 0.7);
            days++;
            percent = (x / population) * 100;
            deaths = deaths + x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) percent + "%). " + (int) deaths + " sind tot.");

            fileWriter.write((int) x + ";" + (int) deaths + "\n");

            return calculateDecrease(x);

        } else {
            System.out.println("In " + days + " Tagen ist niemand mehr infiziert.");
        }
        return x;
    }
}
