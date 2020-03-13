import java.io.FileWriter;
import java.io.IOException;

public class CoronaCalculator {
    static int days;
    static double deaths;
    static int population = 327000000;
    static double prozent;
    static FileWriter myWriter;

    public static void main(String[] args) throws IOException {

        myWriter = new FileWriter("corona.csv");
try {
    myWriter.write("Infizierte;Tote" + "\n");
    double x = 20000;
    x = calculate(x);
    calculateDecrease(x);
    myWriter.close();

} catch (IOException e) {
    System.out.println("An error occurred.");
    e.printStackTrace();
    }
    }

    private static double calculate(double x) throws IOException {

        if (x < population * 0.66) {
            x = x * 1.3;
            days++;
            prozent = (x / population) * 100;
            deaths = x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) prozent + "%). "+ (int) deaths + " sind tot.");

            myWriter.write((int) x + ";" + (int) deaths + "\n");

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
            prozent = (x / population) * 100;
            deaths = deaths + x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) prozent + "%). " + (int) deaths + " sind tot.");

            myWriter.write((int) x + ";" + (int) deaths + "\n");

            return calculateDecrease(x);

        } else {
            System.out.println("In " + days + " Tagen ist niemand mehr infiziert.");
        }
        return x;
    }
}
