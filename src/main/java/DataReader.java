import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.FetchResult;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataReader {

    static String REPODIRECTORY = "../COVID-19";
    private static FileWriter fileWriter;
    private List<String> records;
    private int lastInfected;
    private String lastDate;
    private File repoDir;

    public DataReader(String[] countries) {
        repoDir = new File(REPODIRECTORY);
        cloneRepo();
        this.records = calculateRecords(countries);
        this.lastInfected = calculatelastInfected();
        this.lastDate = calculatelastDate();
  //      printData(data,country);
    }

   private void cloneRepo() {
       try {
           if (repoDir.exists()) {
               Repository repository = new FileRepository(repoDir.getAbsolutePath() + "/.git");
               Git git = new Git(repository);

               PullResult result = git.pull().call();
               FetchResult fetchResult = result.getFetchResult();
               MergeResult mergeResult = result.getMergeResult();
               mergeResult.getMergeStatus();  // this should be interesting
               System.out.println(mergeResult.toString());
           } else {
               Git git = Git.cloneRepository()
                       .setURI( "https://github.com/CSSEGISandData/COVID-19.git" )
                       .setDirectory(repoDir)
                       .call();
           }
       } catch (IOException e) {
       e.printStackTrace();
       } catch (InvalidRemoteException e) {
           e.printStackTrace();
       } catch (TransportException e) {
           e.printStackTrace();
       } catch (GitAPIException e) {
           e.printStackTrace();
       }
    }

    private int calculatelastInfected () {
        String lastSet = records.get(records.size()-1);
        int x = Integer.parseInt(lastSet.split(";")[1]);
        return x;
    }
    private String calculatelastDate () {
        String lastSet = records.get(records.size()-1);
        String lastDate = lastSet.split(";")[0];
        return lastDate;
    }

    private List<String> calculateRecords(String[] countries) {
        List<String> readInRecords = new ArrayList<>();
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
                                //readInRecords.add(file.getName().replaceFirst("[.][^.]+$", "") + ";"+infected+";"+deaths);
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        String[] values = line.split(",");

                        for (String country : countries) {
                            if (values[1].equals(country) && values.length > 3) {
                                int newInfected = Integer.parseInt(values[3]);
                                if (newInfected>0) {
                                    infected += newInfected;
                                    if (values.length > 4 && !values[4].equals("")) {
                                        deaths += Integer.parseInt(values[4]);
                                    }
                                    readInRecords.add(file.getName().replaceFirst("[.][^.]+$", "") + ";"+infected+";"+deaths);
                                }
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
        return readInRecords;
    }

    public List<String> getRecords() {
        return this.records;
    }

    public int getLastInfected () {
        return this.lastInfected;
    }

    public String getLastDate () {
        return this.lastDate;
    }
}
