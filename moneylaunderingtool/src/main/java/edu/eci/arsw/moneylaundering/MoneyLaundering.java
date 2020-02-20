package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering
{
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    public  static AtomicInteger amountOfFilesProcessed;
    private static int numBigFiles = 3 ;
    private static int numHilos = 5;

    public MoneyLaundering()
    {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    
    public List<String> getOffendingAccounts()
    {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList()
    {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/")).filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }
    
    public void divideData(){
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();
        Collections.sort(transactionFiles,new ComparaFile());
        List<File> transactionFilter = new ArrayList<File>();
        List<TransactionProccesor> hilos = new ArrayList<TransactionProccesor>();
        for(int i=0; i<numBigFiles;i++){
            transactionFilter.add(transactionFiles.get(transactionFiles.size()-1));
            transactionFiles.remove(transactionFiles.size()-1);
            hilos.add(new TransactionProccesor(transactionFilter,transactionAnalyzer));
            hilos.get(hilos.size()-1).start();
            transactionFilter = new ArrayList<File>();
            
        }
        int numDiv = transactionFiles.size()/numHilos;
        
        int i = numDiv;
        while(i<transactionFiles.size()){
            for(int j=i-numDiv;j<i;j++){
                transactionFilter.add(transactionFiles.get(j));
            }
            hilos.add(new TransactionProccesor(transactionFilter,transactionAnalyzer));
            hilos.get(hilos.size()-1).start();
            transactionFilter = new ArrayList<File>();
            i+=numDiv;
        }
        if(i!=transactionFiles.size()){
            for(int j=i-numDiv;j<transactionFiles.size();j++){
                transactionFilter.add(transactionFiles.get(j));
            }
            hilos.add(new TransactionProccesor(transactionFilter,transactionAnalyzer));
            hilos.get(hilos.size()-1).start();
            
        }
        
        
        
        
    }

    public static void main(String[] args)
    {
        System.out.println(getBanner());
        System.out.println(getHelp());
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        //Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData());
        //processingThread.start();
        Thread processingThread = new Thread(() -> moneyLaundering.divideData());
        processingThread.start();
        
        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
            {
                System.exit(0);
            }

            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
        }
    }

    private static String getBanner()
    {
        String banner = "\n";
        try {
            banner = String.join("\n", Files.readAllLines(Paths.get("src/main/resources/banner.ascii")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return banner;
    }

    private static String getHelp()
    {
        String help = "Type 'exit' to exit the program. Press 'Enter' to get a status update\n";
        return help;
    }
}