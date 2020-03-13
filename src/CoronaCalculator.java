import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CoronaCalculator {
    static int days;
    static double tote;
    static int anzahloesterreicher  = 8000000;
    static double prozent;
    static FileWriter myWriter;

    public static void main(String[] args) throws IOException {

        myWriter = new FileWriter("corona.csv");
try {
    myWriter.write("Infizierte;Tote" + "\n");
    double x = 150;
    x = calculate(x);
    calculateDecrease(x);
    myWriter.close();

} catch (IOException e) {
    System.out.println("An error occurred.");
    e.printStackTrace();
    }
    }

    private static double calculate(double x) throws IOException {

        if (x < 5300000) {
            x = x * 1.3;
            days++;
            prozent = (x / anzahloesterreicher) * 100;
            tote = x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) prozent + "%). "+ (int) tote + " sind tot.");

            myWriter.write((int) x + ";" + (int) tote + "\n");

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
            prozent = (x / anzahloesterreicher) * 100;
            tote = tote + x * 0.007;
            System.out.println("In " + days + " Tagen sind " + (int) x + " Personen infiziert (" + (int) prozent + "%). " + (int) tote + " sind tot.");

            myWriter.write((int) x + ";" + (int) tote + "\n");

            return calculateDecrease(x);

        } else {
            System.out.println("In " + days + " Tagen ist niemand mehr infiziert.");
        }
        return x;
    }
}
