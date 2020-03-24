package com.fifzu.coronacalculator;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DataReader {
    private List<String> records;
    private int lastInfected;
    private String lastDate;

   public void cloneRepo() throws Exception {
       File repoDir = new File("../COVID-19");

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

    public List<String> getRecords(String[] countries) throws IOException {
        List<String> readInRecords = new ArrayList<>();
        File[] fileList = new File("../COVID-19/csse_covid_19_data/csse_covid_19_daily_reports").listFiles();
        Arrays.sort(fileList);

        for (File file : fileList) {
            String filecheck = file.getName().substring(file.getName().length() - 4);
            if (filecheck.contains(".csv")) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    int infected = 0;
                    int deaths = 0;
                    String line = "";
                    while (true) {
                        if (!((line = br.readLine()) != null)) {
                            if (infected > 0 || deaths > 0) {
                                readInRecords.add(file.getName().replaceFirst("[.][^.]+$", "") + ";" + infected + ";" + deaths+";false");
                            }
                            break;
                        }

                        String[] values = line.split(",");

                        for (String country : countries) {
                            if (values[1].equals(country) && values.length > 3) {
                                int newInfected = Integer.parseInt(values[3]);
                                infected += newInfected;
                                if (values.length > 4 && !values[4].equals("")) {
                                    deaths += Integer.parseInt(values[4]);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (readInRecords.size()>0){
            this.records = readInRecords;
            this.lastInfected = calculatelastInfected();
            this.lastDate = calculatelastDate();
        }

        return this.records;
    }

    public int getLastInfected () {
        return this.lastInfected;
    }

    public String getLastDate () {
        return this.lastDate;
    }


}
