import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataReader {

    static FileWriter fileWriter;
    public DataReader(String country) {
        List<String> data;

        data = getData(country);

        printData(data,country);
    }

    private List<String> getData (String country) {
        List<String> records = new ArrayList<>();
        File[] fileList = new File("../COVID-19/csse_covid_19_data/csse_covid_19_daily_reports").listFiles();
        Arrays.sort(fileList);

        for (File file : fileList) {
            String filecheck = file.getName().substring(file.getName().length()-4);
            if (filecheck.contains(".csv"))
            {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    int infected = 0;
                    int deaths = 0;
                    String line = "";
                    while (true) {
                        try {
                            if (!((line = br.readLine()) != null)){
                                records.add(file.getName().replaceFirst("[.][^.]+$", "") + ";"+infected+";"+deaths);
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String[] values = line.split(",");

                        if (values[1].equals(country) && values.length>3) {
                            infected += Integer.parseInt(values[3]);
                            if (values.length>4 && !values[4].equals("")) {
                                deaths += Integer.parseInt(values[4]);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
    }

    private void printData (List<String> records,String country)  {

        try {
            fileWriter = new FileWriter(country + ".csv");
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
}
