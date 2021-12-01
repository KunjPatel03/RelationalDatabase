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
                OutputStream outputStream = new FileOutputStream(destinationSchemaFile);

                byte[] buffer = new byte[1024];

                int length;

                while ((length = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();
            }
            return true;
        } else{
            printer.printString("Please enter correct path of the folder.");
            return false;
        }
    }
}
