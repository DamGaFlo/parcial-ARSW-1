package edu.eci.arsw.moneylaundering;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransactionAnalyzer {
    private static HashMap<String, Integer> smallTransactionsPerAccount;
    private static final int LAUNDERING_LIMIT_AMOUNT = 1250;
    private static final int LAUNDERING_LIMIT_COUNT = 100;

    public TransactionAnalyzer()
    {
        smallTransactionsPerAccount = new HashMap<>();
    }

    public synchronized void addTransaction(Transaction transaction)
    {
        if(transaction.amount < LAUNDERING_LIMIT_AMOUNT)
            synchronized(smallTransactionsPerAccount){
                System.out.println("new");
                {
                    String destinationAccount = transaction.destinationAccount;
                    if(!smallTransactionsPerAccount.containsKey(destinationAccount))
                    {
                        System.out.println("new 1");
                        smallTransactionsPerAccount.put(destinationAccount, 1);
                    }
                    else
                    {
                        smallTransactionsPerAccount.put(destinationAccount, smallTransactionsPerAccount.get(destinationAccount) + 1);
                        System.out.println("new 2");
                    }
                    if(smallTransactionsPerAccount.get(destinationAccount) > LAUNDERING_LIMIT_COUNT)
                        AccountReporter.report(destinationAccount, smallTransactionsPerAccount.get(destinationAccount));
                }
            }
    }

    public List<String> listOffendingAccounts()
    {
        synchronized(smallTransactionsPerAccount){
            return smallTransactionsPerAccount.entrySet().stream().filter(accountEntry-> accountEntry.getValue()>LAUNDERING_LIMIT_COUNT).map(Map.Entry::getKey).collect(Collectors.toList());
    
        
        }
    }

}
