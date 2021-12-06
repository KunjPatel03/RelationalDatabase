package Controller;

import View.printer.Printer;

import java.io.*;

public class ExportDump {
    private File databaseToExport;
    private String databaseDumpFolderName;
    private File databaseDumpFolder;
    private File parentFolder;
    private Printer printer;

    public ExportDump(File database, String destination, Printer print){
        this.databaseToExport = database;
        this.databaseDumpFolderName = destination + database.getName() + "Dump";
        this.databaseDumpFolder = new File(databaseDumpFolderName);
        this.parentFolder = new File(destination);
        this.printer = print;
    }

    public boolean downloadSchema() throws IOException {
        if(parentFolder.exists()){
            if(!databaseDumpFolder.exists()){
                databaseDumpFolder.mkdir();
            }
            String[] schemas = databaseToExport.list();
            for (String schema : schemas){
                File sourceSchemaFile = new File(databaseToExport, schema);
                File destinationSchemaFile = new File(databaseDumpFolder, schema);
                InputStream inputStream = new FileInputStream(sourceSchemaFile);
                BufferedReader inputBufferReader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = new FileOutputStream(destinationSchemaFile);
                BufferedWriter outputBufferWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                String line;
                String insertQuery = "-----\n----- Dumping data for table `" + schema + "`\n" + "-----\nINSERT INTO `" + schema +"` VALUES (";
                String createTableQuery = "";
                boolean readHeader = true;
                while ((line = inputBufferReader.readLine()) != null){
                    if(line.indexOf('$') != -1){
                        if(readHeader){
                            readHeader = false;
                        } else {
                            String[] values = line.split("\\|\\|");
                            for(int i = 0; i < values.length - 1; i++){
                                values[i] = values[i].replaceAll("\\$", "");
                                insertQuery += values[i];
                                if(i < values.length-2){
                                    insertQuery += ", ";
                                }
                            }
                            insertQuery += "), (";
                        }
                    }
                }
                insertQuery = insertQuery.substring(0, insertQuery.length()-3) + ";";
                outputBufferWriter.write(insertQuery);
                outputBufferWriter.newLine();
                inputBufferReader.close();
                outputBufferWriter.close();
            }
            return true;
        } else{
            printer.printString("Please enter correct path of the folder.");
            return false;
        }
    }
}
