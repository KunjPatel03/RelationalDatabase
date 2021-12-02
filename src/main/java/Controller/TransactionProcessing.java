package Controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class TransactionProcessing {

    ProcessQuery processorQuery = new ProcessQuery();
    ArrayList<String> queriesTobeExecuted;
    boolean isAllQueryValid=true;
    private static FileWriter fileWriter;
    ExtractTableName extractTableName= new ExtractTableName();
    ArrayList<String> tableNames;
    boolean tableUnLocked=true;

    public void validTransaction(StringBuilder stringBuilder) throws Exception {
        try {
            fileWriter = new FileWriter("./src/main/java/Logs/Transaction.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileWriter.write("TRANSACTION STARTED AT "+ new Timestamp(System.currentTimeMillis())+"\n");
        fileWriter.close();
        String[] queries = stringBuilder.toString().split(";");
        queriesTobeExecuted= new ArrayList<>();
        for (int i=1; i<queries.length-1; i++) {
            System.out.println("query = " + queries[i]);
            if (processorQuery.isQueryValidForTransaction(queries[i])){
                queriesTobeExecuted.add(queries[i]+";");
            }else
            {
                isAllQueryValid=false;
            }

        }
        if(isAllQueryValid)
        {//Code if all of the transaction gets executed
            tableNames=new ArrayList<>();
            for(int i=0;i<queriesTobeExecuted.size();i++)
            {
                if(queriesTobeExecuted.get(i).contains("create")){
                    tableNames.add(extractTableName.getCreateQueryTableName(queriesTobeExecuted.get(i)));
                }else if (queriesTobeExecuted.get(i).contains("select")){
                    tableNames.add(extractTableName.getSelectQueryTableName(queriesTobeExecuted.get(i)));

                }else if (queriesTobeExecuted.get(i).contains("select")&&queriesTobeExecuted.get(i).contains("where")){
                    tableNames.add(extractTableName.getSelectWithConditionQueryTableName(queriesTobeExecuted.get(i)));

                }else if (queriesTobeExecuted.get(i).contains("insert")){
                    tableNames.add(extractTableName.getInsertQueryTableName(queriesTobeExecuted.get(i)));

                }else if (queriesTobeExecuted.get(i).contains("update")){
                    tableNames.add(extractTableName.getUpdateWithConditionQueryTableName(queriesTobeExecuted.get(i)));

                }
                else if (queriesTobeExecuted.get(i).contains("delete")){
                    tableNames.add(extractTableName.getDeleteWithConditionQueryTableName(queriesTobeExecuted.get(i)));

                }


            }
            executeQueries();
        }else
        {
            throw new Exception("Sorry you must have entered invalid query");
        }


    }

    public void executeQueries() throws Exception {
        String currentDb = UContext.getCurrentDb();
        for (String table: tableNames ) {
            File file = new File("./src/main/java/Model/database/"+currentDb+"/"+table+".txt");
            Scanner sc = new Scanner(file);
            String status =sc.nextLine();
            if (status.equalsIgnoreCase("[LOCKED]")){
                tableUnLocked=false;
            }
        }

        if (tableUnLocked){
            for (String table: tableNames ) {
                File file = new File("./src/main/java/Model/database/"+currentDb+"/"+table+".txt");
                Scanner sc;
                String lines ="";
                try {
                    sc = new Scanner(file);
                    while (sc.hasNextLine())
                        lines = lines + sc.nextLine()+"\n";

                    lines= lines.replaceAll("UNLOCKED", "LOCKED");
                    System.out.println(lines);
                    FileWriter fileWriter = new FileWriter("./src/main/java/Model/database/"+currentDb+"/"+table+".txt");
                    fileWriter.write(lines);
                    fileWriter.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            for (String query: queriesTobeExecuted) {
                processorQuery.processorQuery(query);
            }

            for (String table: tableNames ) {
                File file = new File("./src/main/java/Model/database/"+currentDb+"/"+table+".txt");
                Scanner sc;
                String lines ="";
                try {
                    sc = new Scanner(file);
                    while (sc.hasNextLine())
                        lines = lines + sc.nextLine()+"\n";

                    lines= lines.replaceAll("LOCKED", "UNLOCKED");
                    System.out.println(lines);
                    FileWriter fileWriter = new FileWriter("./src/main/java/Model/database/"+currentDb+"/"+table+".txt");
                    fileWriter.write(lines);
                    fileWriter.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }else {
            TimeUnit.SECONDS.sleep(20);
            executeQueries();
        }

    }

}