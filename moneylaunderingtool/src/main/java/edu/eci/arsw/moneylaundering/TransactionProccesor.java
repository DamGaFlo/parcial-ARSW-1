/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.List;

/**
 *
 * @author johan.garrido
 */
public class TransactionProccesor extends Thread{
    
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    List<File> transactionFiles;
    
    
    public TransactionProccesor(List<File> transactionFiles,TransactionAnalyzer transactionAnalyzer){
        this.transactionAnalyzer = transactionAnalyzer;
        transactionReader = new TransactionReader();
        this.transactionFiles = transactionFiles;
    }
    
    public void run(){
        
        for(File transactionFile : transactionFiles)
        {            
            List<Transaction> transactions = transactionReader.readTransactionsFromFile(transactionFile);
            for(Transaction transaction : transactions)
            {
                transactionAnalyzer.addTransaction(transaction);
            }
            MoneyLaundering.amountOfFilesProcessed.incrementAndGet();
        }
        
        System.out.println(transactionAnalyzer.listOffendingAccounts());
        
    
    }
   
        
        

}
