package Controller;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessQuery {

    private static GeneralLoggingController generalLoggingController = null;

    public ProcessQuery() {
        this.generalLoggingController = new GeneralLoggingController();
        generalLoggingController.writeLog("QUERY PROCESSOR INVOKED/STARTED AT ", System.currentTimeMillis());
    }

    public static final String CREATE_DATABASE_QUERY =
            "create database";
    public static final String CREATE_DATABASE_QUERY_STRING =
            "^(?i)(CREATE\\sDATABASE\\s[a-zA-Z\\d]+;)$";
    public static final String USE_DATABASE_QUERY =
            "use database";
    public static final String USE_DATABASE_QUERY_STRING =
            "^(?i)(USE\\sDATABASE\\s[a-zA-Z\\d]+;)$";
    private String databaseName = "";
    public static final String CREATE_TABLE_QUERY =
            "create table";
    public static final String USE_CREATE_TABLE_QUERY_STRING =
            "^(?i)(CREATE\\sTABLE\\s[a-zA-Z\\d]+\\s\\(([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)" +
                    "(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))" +
                    "?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)" +
                    "(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)\\);)$";
    public static final String CREATE_TABLE_COLUMN_STRING =
            "([a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\" +
                    "([a-zA-Z\\d]+\\))?(,\\s[a-zA-Z\\d]+\\s(INT|TEXT|FLOAT|BOOLEAN)" +
                    "(\\sPRIMARY KEY|\\sREFERENCES\\s[a-zA-Z\\d]+\\([a-zA-Z\\d]+\\))?)*)";
    public static final String INSERT_QUERY =
            "insert";
    public static final String INSERT_QUERY_SYNTAX =
            "^(?i)(INSERT\\sINTO\\s[a-zA-Z\\d]+\\s\\([a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\)" +
                    "\\sVALUES\\s\\(\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?" +
                    "(,\\s\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?)*\\);)$";
    public static final String INSERT_COLUMN_NAME_STRING =
            "([a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\))";

    public static final String INSERT_VALUES_STRING =
            "VALUES\\s\\(\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?" +
                    "(,\\s\\\"?[a-zA-Z\\d\\s~`!@#$^&*-_+=|':;.,?]+\\\"?)*\\)";

    public static final String SELECT_QUERY =
            "select";
    public static final String SELECT_QUERY_SYNTAX =
            "^(?i)(SELECT\\s[a-zA-Z\\d]+(,\\s[a-zA-Z\\d]+)*\\sFROM\\s[a-zA-Z\\d]+;)$";
    public static final String SELECTWITHCONDITION_QUERY_SYNTAX =
            "^(?i)(SELECT\\s.*FROM\\s.*WHERE\\s.*)$";

    public static final String UPDATE_QUERY =
            "update";
    public static final String UPDATEWITHCONDITION_QUERY_SYNTAX =
            "^(?i)(UPDATE\\s.*SET\\s.*WHERE\\s.*)$";

    public static final String TRUNCATE_TABLE_QUERY =
            "truncate table";
    public static final String TRUNCATE_TABLE_QUERY_STRING =
            "^(?i)(TRUNCATE\\sTABLE\\s[a-zA-Z\\d]+;)$";

   public static final String DELETE_TABLE_QUERY=
           "delete";
   public static final String DELETEWITHCONDITION_QUERY_SYNTAX=
           "^(?i)(DELETE\\s.*FROM\\s.*WHERE\\s.*)$";

   public static final String CREATE_DATABASE = "CREATE DATABASE ";

   public static final String USE_DATABASE = "USE DATABASE ";

   public static final String CREATE_TABLE = "CREATE TABLE ";

   public static final String INSERT_INTO = "INSERTING VALUES ";

   public static final String SELECT_STATEMENT = "SELECT STATEMENT ";

   public static final String UPDATE_STATEMENT = "UPDATE STATEMENT ";

   public static final String DELETE_STATEMENT = "DELETE STATEMENT ";

   public static final String DROP_STATEMENT = "DROP STATEMENT ";

   public static final String EXECUTED = " EXECUTED AT ";

   public static final String PROGRAM_EXIT = "EXITING QUERY PROCESSOR AT ";

    public static final String TRANSACTION_TABLE_QUERY =
            "start transaction";
    public static final String START_TRANSACTION_QUERY_STRING =
            "^(?i)START TRANSACTION;$";
    public static final String COMMIT_TABLE_QUERY =
            "commit";
    public static final String COMMIT_QUERY_STRING =
            "^(?i)COMMIT;$";
    public static final String ROLLBACK_TABLE_QUERY =
            "rollback";
    public static final String ROLLBACK_QUERY_STRING =
            "^(?i)ROLLBACK;$";
    public static final String DROP_TABLE_QUERY =
            "drop database";
    public static final String DROP_DATABASE_QUERY_STRING =
            "^(?i)(DROP\\sDATABASE\\s[a-zA-Z\\d]+;)$";


    public static final String path = "./src/main/java/Model/database/";
    public static final String inMemoryPath = "./src/main/java/Model/inMemory/";
    public static boolean isTransaction = false;
    public static String DBPATH = path;
    public String processorQuery(String query) throws Exception {
        String returnMessage = null;
        if(isQueryValid(query)){
            if(Pattern.matches(CREATE_DATABASE_QUERY_STRING, query)) {
                returnMessage = executeCreateDatabaseQuery(query);
            }
            else if (Pattern.matches(USE_DATABASE_QUERY_STRING, query)){
                returnMessage = executeUseDatabaseQuery(query);
            }
            else if (Pattern.matches(USE_CREATE_TABLE_QUERY_STRING, query)){
                returnMessage = executeCreateTableQuery(query);
            }
            else if (Pattern.matches(INSERT_QUERY_SYNTAX, query)){
                returnMessage = executeInsertQuery(query);
            }
            else if (Pattern.matches(SELECT_QUERY_SYNTAX, query)){
                returnMessage = executeSelectQuery(query);
            }
            else if (Pattern.matches(SELECTWITHCONDITION_QUERY_SYNTAX, query)){
                returnMessage = executeSelectWithConditionQuery(query);
            }
            else if (Pattern.matches(UPDATEWITHCONDITION_QUERY_SYNTAX, query)){
                returnMessage = executeUpdateWithConditionQuery(query);
            }
            else if (Pattern.matches(TRUNCATE_TABLE_QUERY_STRING, query)){
                returnMessage = executeDropTableQuery(query);
            }
            else if (Pattern.matches(DELETEWITHCONDITION_QUERY_SYNTAX, query)){
                returnMessage = executeDeleteWithConditionQuery(query);
            }
            else if (Pattern.matches(START_TRANSACTION_QUERY_STRING, query)){
                returnMessage = executeTransaction(query);
            }
            else if (Pattern.matches(COMMIT_QUERY_STRING, query)){
                returnMessage = executeCommit(query);
            }
            else if (Pattern.matches(ROLLBACK_QUERY_STRING, query)){
                returnMessage = executeRollBack(query);
            }
            else if (Pattern.matches(DROP_DATABASE_QUERY_STRING, query)){
                returnMessage = executeDropDatabaseQuery(query);
            }
        }else{
            throw new Exception("Invalid query !!!!");
        }
       return returnMessage;
    }
    private Boolean isQueryValid(String query) throws Exception {
        final String Query = query.trim().toLowerCase();
        if (Query.contains(CREATE_DATABASE_QUERY)) {
            if(!Pattern.matches(CREATE_DATABASE_QUERY_STRING, Query)){
                throw new Exception("Invalid CREATE DATABASE query !!!!");
            }
        }else if(Query.contains(USE_DATABASE_QUERY)){
            if(!Pattern.matches(USE_DATABASE_QUERY_STRING, Query)){
                throw new Exception("Invalid USE DATABASE query !!!!");
            }
        }
        else if(Query.contains(CREATE_TABLE_QUERY)){
            if(!Pattern.matches(USE_CREATE_TABLE_QUERY_STRING, Query)){
                throw new Exception("Invalid create table query !!!!");
            }
        }
        else if(Query.contains(INSERT_QUERY)){
            if(!Pattern.matches(INSERT_QUERY_SYNTAX, Query)){
                throw new Exception("Invalid insert query !!!!");
            }
        }
        else if(Query.contains(SELECT_QUERY)){
            return true;
        }
        else if(Query.contains(UPDATE_QUERY)){
            if(!Pattern.matches(UPDATEWITHCONDITION_QUERY_SYNTAX, Query)){
                throw new Exception("Invalid update query !!!!");
            }
        }else if(Query.contains(TRUNCATE_TABLE_QUERY)){
            if(!Pattern.matches(TRUNCATE_TABLE_QUERY_STRING, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }else if(Query.contains(DELETE_TABLE_QUERY)){
            if(!Pattern.matches(DELETEWITHCONDITION_QUERY_SYNTAX, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }
        else if(Query.contains(TRANSACTION_TABLE_QUERY)){
            if(!Pattern.matches(START_TRANSACTION_QUERY_STRING, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }
        else if(Query.contains(COMMIT_TABLE_QUERY)){
            if(!Pattern.matches(COMMIT_QUERY_STRING, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }
        else if(Query.contains(ROLLBACK_TABLE_QUERY)){
            if(!Pattern.matches(ROLLBACK_QUERY_STRING, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }
        else if(Query.contains(DROP_TABLE_QUERY)){
            if(!Pattern.matches(DROP_DATABASE_QUERY_STRING, Query)) {
                throw new Exception("Invalid drop table query !!!!");
            }
        }
        return true;
    }
    private String executeCreateDatabaseQuery(String query) throws IOException {
        String dbName = query.substring(0,query.length()-1).split(" ")[2];

        String message = CREATE_DATABASE+" || "+dbName+EXECUTED;
        generalLoggingController.writeLog(message, System.currentTimeMillis());
        String path = DBPATH + dbName;

        File databasePath = new File(path);
        Lock.exclusiveLock(dbName,null);
        databasePath.mkdirs();
        Lock.removeExclusiveLock(dbName,null);
        return "CREATED DB SUCCESSFULLY !!!";
    }

    private String executeUseDatabaseQuery(String query) throws IOException {
        String dbName = query.substring(0,query.length()-1).split(" ")[2];
        final File[] files = new File(DBPATH).listFiles();
        for (final File file : files) {
            if (file.getName().equalsIgnoreCase(dbName)) {
                this.databaseName = file.getName();
                String message = USE_DATABASE+" || "+this.databaseName+EXECUTED;
                generalLoggingController.writeLog(message, System.currentTimeMillis());

            }
        }
        return this.databaseName + " HAS BEEN SELECTED SUCCESSFULLY !!!";
    }
    private String executeCreateTableQuery(String query) throws Exception {
        String tableName = query.substring(0,query.length()-1).split(" ")[2];
        String path = DBPATH+ this.databaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();

        final Pattern pattern = Pattern.compile(CREATE_TABLE_COLUMN_STRING);
        final Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            final String[] columnNames = matcher.group().split(",");
            try (final FileWriter fileWriter = new FileWriter(tablePath+tableName+".txt")) {
                String message = CREATE_TABLE+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
                generalLoggingController.writeLog(message, System.currentTimeMillis());
                final StringBuilder stringBuilder = new StringBuilder();
                for (final String columnToken : columnNames) {
                    final String[] tokens = columnToken.trim().split(" ");
                    if (tokens.length == 2) {
                        stringBuilder.append(tokens[0])
                                .append("(").append(tokens[1])
                                .append(")")
                                .append("$||$");
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("PRIMARY")) {
                        stringBuilder.append(tokens[0])
                                .append("(").append(tokens[1]).append("|")
                                .append("PK")
                                .append(")")
                                .append("$||$");
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("REFERENCES")) {
                        final String foreignKeyTable = tokens[3].split("\\(")[0];
                        String foreignKeyCol = tokens[3].split("\\(")[1]
                                .replaceAll("\\)", "");
                        stringBuilder.append(tokens[0]).append("(")
                                .append(tokens[1]).append("|")
                                .append("FK").append("|")
                                .append(foreignKeyTable).append("|")
                                .append(foreignKeyCol)
                                .append(")")
                                .append("$||$");
                    }
                }
                stringBuilder.replace(stringBuilder.length()
                        - "$||$".length(), stringBuilder.length(), "");
                stringBuilder.append("\n");
                fileWriter.append(stringBuilder.toString());
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        return "TABLE HAS BEEN CREATED SUCCESSFULLY !!!";
    }

    private String executeInsertQuery(String query) throws Exception {
        String tableName = query.substring(0,query.length()-1).split(" ")[2];

        String path =DBPATH+ this.databaseName;

        String message = INSERT_INTO+" - "+this.databaseName+" -> "+tableName+EXECUTED;
        generalLoggingController.writeLog(message, System.currentTimeMillis());

        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        final Pattern pattern = Pattern.compile(INSERT_COLUMN_NAME_STRING);
        final Matcher insertMatcher = pattern.matcher(query);
        final Pattern pattern1 = Pattern.compile(INSERT_VALUES_STRING);
        final Matcher valuesMatcher = pattern1.matcher(query);
        if(insertMatcher.find()){
            final String ColumnNames = insertMatcher.group();
            final String[] columnNamesArray = ColumnNames
                    .replace(")", "").split(",");
            final Set<String> columnNames = new HashSet<>(Arrays.asList(columnNamesArray));
            final int numberOfColumns = columnNames.size();
            if (valuesMatcher.find()) {
                final String ColumnValues = valuesMatcher.group().substring(8,
                        valuesMatcher.group().length() - 1);
                final String[] columnValues = ColumnValues
                        .replace("\"", "").split(",");
                final Map<String, String> columnValue = new LinkedHashMap<>();
                for (int i = 0; i < numberOfColumns; i++) {
                    columnValue.put(columnNamesArray[i].trim(), columnValues[i].trim());
                }
                try (final FileWriter fileWriter = new FileWriter(tablePath+tableName+".txt"
                        , true);
                     final BufferedReader bufferedReader = new BufferedReader(new
                             FileReader(tablePath+tableName+".txt"))) {
                    final String columnNamesFromFile = bufferedReader.readLine();
                    final String[] columnNamesExtracted = columnNamesFromFile
                            .split("\\$\\|\\|\\$");

                    final LinkedHashMap<String, String> columnDetails = new LinkedHashMap<>();
                    for (final String column : columnNamesExtracted) {
                        final String[] temporaryTokens = column
                                .replace(")", "").split("\\(");
                        columnDetails.put(temporaryTokens[0]
                                .replace("(", ""), temporaryTokens[1]
                                .split("\\|")[0]);
                    }
                    for (int i = 0; i < columnNamesExtracted.length; i++) {
                        String[] temporaryTokens = columnNamesExtracted[i]
                                .replace(")", "").split("\\(");
                    }
                    final StringBuilder stringBuilder = new StringBuilder();
                    for (final String columnName : columnValue.keySet()) {
                        final String columnDataType = columnDetails.get(columnName);
                        final String column_value = columnValue.get(columnName);
                        if (columnDataType.equalsIgnoreCase("INT")) {
                            try {
                                Integer.parseInt(column_value);
                            } catch (final NumberFormatException e) {

                            }
                        }
                        if (columnDataType.equalsIgnoreCase("FLOAT")) {
                            try {
                                Float.parseFloat(column_value);
                            } catch (final NumberFormatException e) {

                            }
                        }
                        if (columnDataType.equalsIgnoreCase("BOOLEAN")) {
                            boolean value = Boolean.parseBoolean(column_value);
                        }
                        stringBuilder.append(column_value).append("$||$");
                    }
                    stringBuilder.replace(stringBuilder.length() - "$||$".length(),
                            stringBuilder.length(), "");
                    stringBuilder.append("\n");
                    fileWriter.append(stringBuilder.toString());
                }
            }
        }
        return "RECORD HAS BEEN INSERTED SUCCESSFULLY !!!";
    }

    private String executeSelectQuery(String query) throws Exception {
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = queryArray[queryArray.length-1];
        String message = SELECT_STATEMENT+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
        generalLoggingController.writeLog(message, System.currentTimeMillis());
        String path = DBPATH+ this.databaseName;

        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new Exception("Table doesn't exists");
        }
        final String FullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(FullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder stringBuilder = new StringBuilder();
            boolean isHeading = true;
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                final String[] columns = content.split("\\$\\|\\|\\$");
                stringBuilder.append("| ");
                if (isHeading) {
                    for (final String column : columns) {
                        stringBuilder.append(column.split("\\(")[0]).append(" | ");
                    }
                    stringBuilder.append("\n");
                    isHeading = false;
                } else {
                    for (final String column : columns) {
                        stringBuilder.append(column).append(" | ");
                    }
                    stringBuilder.append("\n");
                }
            }
            System.out.println(stringBuilder);
        }
        return "QUERY HAS BEEN SELECTED SUCCESSFULLY!!!";
    }
    private String executeSelectWithConditionQuery(String query) throws Exception {
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        int from_index=0;
        String tableName = "";
        String colName = "";
        String value = "";
        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("from")){
                tableName = queryArray[i+1];
                String message = SELECT_STATEMENT+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
                generalLoggingController.writeLog(message, System.currentTimeMillis());
            }
            if(queryArray[i].equalsIgnoreCase("where")){
                colName = queryArray[i+1];
                value = queryArray[i+3];
            }

        }
        String path =DBPATH+ this.databaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new Exception("Table doesn't exists");
        }
        final String FullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(FullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder stringBuilder = new StringBuilder();
            boolean isHeading = true;
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                final String[] columns = content.split("\\$\\|\\|\\$");
                if (isHeading) {
                    for (final String column : columns) {
                        stringBuilder.append(column.split("\\(")[0]).append(" | ");
                    }
                    stringBuilder.append("\n");
                    isHeading = false;
                } else {
                    boolean contains = Arrays.stream(columns).anyMatch(value::equals);
                    if(contains){
                        for (final String column : columns) {
                                stringBuilder.append(column).append(" | ");
                        }
                        stringBuilder.append("\n");
                        break;
                    }
                }
            }
            System.out.println(stringBuilder);
        }
        return "QUERY HAS BEEN SELECTED SUCCESSFULLY!!!";
    }
    private String executeUpdateWithConditionQuery(String query) throws Exception {
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = "";
        String colName = "";
        String value = "";
        String updateName = "";
        String updateValue = "";
        int colIndex = 0;
        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("update")){
                tableName = queryArray[i+1];
                String message = UPDATE_STATEMENT+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
                generalLoggingController.writeLog(message, System.currentTimeMillis());
            }
            if(queryArray[i].equalsIgnoreCase("where")){
                colName = queryArray[i+1];
                value = queryArray[i+3];
            }
            if(queryArray[i].equalsIgnoreCase("set")){
                updateName = queryArray[i+1];
                updateValue = queryArray[i+3];
            }
        }

        String path = DBPATH+ this.databaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new Exception("Table doesn't exists");
        }
        final String FullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(FullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder stringBuilder = new StringBuilder();
            boolean isHeading = true;
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                final String[] columns = content.split("\\$\\|\\|\\$");

                int index = 0;
                if (isHeading) {
                    for (final String column : columns) {

                        if(column.split("\\(")[0].equalsIgnoreCase(updateName)){
                            colIndex = index;
                        }
                        stringBuilder.append(column).append("$||$");
                        index++;
                    }
                    stringBuilder.append("\n");
                    isHeading = false;
                } else {
                    boolean contains = Arrays.stream(columns).anyMatch(value::equals);

                    if(contains){
                        columns[colIndex] = updateValue;
                        for (final String column : columns) {
                            stringBuilder.append(column).append("$||$");
                        }
                        stringBuilder.append("\n");

                    }else{
                        for (final String column : columns) {
                            stringBuilder.append(column).append("$||$");
                        }
                        stringBuilder.append("\n");
                    }
                }
            }
            System.out.println(stringBuilder);
            File file = new File(FullPath);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(stringBuilder));
            bw.close();
        }
        return "UPDATE QUERY HAS BEEN SELECTED SUCCESSFULLY!!!";
    }

    private String executeDeleteWithConditionQuery(String query) throws Exception {
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = "";
        String value = "";
        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("from")){
                tableName = queryArray[i+1];
                String message = DELETE_STATEMENT+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
                generalLoggingController.writeLog(message, System.currentTimeMillis());
            }
            if(queryArray[i].equalsIgnoreCase("where")){
                value = queryArray[i+3];
            }
        }

        String path ="./src/main/java/Model/database/"+ this.databaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new Exception("Table doesn't exists");
        }
        final String FullPath = tablePath + tableName + ".txt";
        try (final FileReader fileReader = new FileReader(FullPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            final StringBuilder stringBuilder = new StringBuilder();
            boolean isHeading = true;
            String content;
            while ((content = bufferedReader.readLine()) != null) {
                final String[] columns = content.split("\\$\\|\\|\\$");
                if (isHeading) {
                    for (final String column : columns) {
                        stringBuilder.append(column).append("$||$");
                    }
                    stringBuilder.append("\n");
                    isHeading = false;
                } else {
                    boolean contains = Arrays.stream(columns).anyMatch(value::equals);
                    if(!contains){
                        for (final String column : columns) {
                            stringBuilder.append(column).append("$||$");
                        }
                        stringBuilder.append("\n");
                    }
                }
            }
            System.out.println(stringBuilder);
            File file = new File(FullPath);
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(stringBuilder));
            bw.close();
        }
        return "DELETE QUERY HAS BEEN EXECUTED SUCCESSFULLY!!!";
    }
    private String executeDropTableQuery(String query) throws Exception {
        String tableName = query.substring(0,query.length()-1).split(" ")[2];
        String path =DBPATH+ this.databaseName;
        String message = DROP_STATEMENT+" || "+this.databaseName+" |$ "+tableName+EXECUTED;
        generalLoggingController.writeLog(message, System.currentTimeMillis());
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = DBPATH + this.databaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        boolean isTableExists = false;
        for (final File table : allTables) {
            if (table.getName().equalsIgnoreCase(tableName + ".txt")) {
                final boolean isTableDeleted = table.delete();
                isTableExists = true;
            }
        }
        if (!isTableExists) {
            throw new Exception("Table doesn't exists");
        }
        return "TABLE HAS BEEN DROPPED SUCCESSFULLY!!!";
    }


    public void closeFileWriter()  {
        try {
            generalLoggingController.writeLog(PROGRAM_EXIT, System.currentTimeMillis());
            generalLoggingController.createFile(GeneralLoggingController.GeneralLogLocation).close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private String executeTransaction(String query) throws Exception {
        final String tablePath = path + this.databaseName + "/";
        final String inMemoryPath = "./src/main/java/Model/inMemory/" + this.databaseName + "/";
        final File inMemoryDatabase = new File(inMemoryPath);
        if (inMemoryDatabase.mkdirs()) {
            final File dbFolder = new File(tablePath);
            final File[] dbTables = dbFolder.listFiles();
            for (final File table : dbTables) {
                System.out.println(table);
                final Path src = Paths.get(tablePath+ table.getName());
                final Path dest = Paths.get(inMemoryPath+ table.getName());
                try {
                    Files.copy(src, dest);
                } catch (final IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            useDifferentPath(true);
        }

        return "";
    }
    private String executeCommit(String query) throws Exception {
        final String tablePath = DBPATH + this.databaseName + "/";
        final String inMemoryPath = "./src/main/java/Model/inMemory/" + this.databaseName + "/";
        final File inMemoryDatabase = new File(inMemoryPath);
        final String inServerDatabasePath = path + "/" + this.databaseName + "/";
        final File dbFolder = new File(inServerDatabasePath);
        final File[] allTables = dbFolder.listFiles();
        for (final File table : allTables) {
            table.delete();
        }
        dbFolder.delete();
        if (dbFolder.mkdirs()) {
            final File[] inMemoryDatabaseTables = inMemoryDatabase.listFiles();
            for (final File table : inMemoryDatabaseTables) {
                final Path src = Paths.get(inMemoryPath + table.getName());
                final Path dest = Paths.get(inServerDatabasePath + table.getName());
                try {
                    Files.copy(src, dest);
                } catch (final IOException e) {
                    System.out.println(e.getMessage());
                }
            }
            for (final File table : inMemoryDatabaseTables) {
                table.delete();
            }
            inMemoryDatabase.delete();
            useDifferentPath(false);
        }
        return "";
    }

    private String executeRollBack(String query) throws Exception {
        final String inMemoryDatabasePath = DBPATH + this.databaseName;
        final File inMemoryDatabase = new File(inMemoryDatabasePath);
        final File[] allTables = inMemoryDatabase.listFiles();
        for (final File table : allTables) {
            table.delete();
        }
        inMemoryDatabase.delete();
        return "";
    }

    public static void useDifferentPath(boolean isTransaction) {
        ProcessQuery.isTransaction = isTransaction;
        DBPATH = (isTransaction) ? inMemoryPath: path;
    }

    private String executeDropDatabaseQuery(String query) throws IOException {
        String dbName = query.substring(0,query.length()-1).split(" ")[2];
        final File[] allTables = new File(DBPATH+dbName).listFiles();
        for (final File table : allTables) {
            table.delete();
        }
        return dbName + " HAS BEEN DROPPED SUCCESSFULLY !!!";
    }
}
