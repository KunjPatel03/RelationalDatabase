package Controller;

import Controller.ProcessQuery;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class TransactionProcessing {

    ProcessQuery processorQuery = new ProcessQuery();
    ArrayList<String> queriesTobeExecuted;
    boolean isAllQueryValid=true;
    private static FileWriter fileWriter;
    public void validTransaction(StringBuilder stringBuilder) throws Exception {
        try {
            fileWriter = new FileWriter("./src/main/java/Logs/Transaction.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileWriter.write("TRANSACTION STARTED AT "+ new Timestamp(System.currentTimeMillis())+"\n");
        String[] queries = stringBuilder.toString().split(";");
        queriesTobeExecuted= new ArrayList<>();
        for (int i=1; i<queries.length-1; i++) {
            System.out.println("query = " + queries[i]);
            if (processorQuery.isQueryValidForTransaction(queries[i])){
                queriesTobeExecuted.add(queries[i]);
            }else
            {
                isAllQueryValid=false;
            }

        }
        if(isAllQueryValid)
        {//Code if all of the transaction gets executed
            for(int i=0;i<queriesTobeExecuted.size();i++)
            {
                System.out.println(queriesTobeExecuted.get(i));
            }
        }else
        {
            System.out.println("Sorry you must have entered invalid query");
        }




        //System.out.println("stringBuilder = " + stringBuilder);


    }

}