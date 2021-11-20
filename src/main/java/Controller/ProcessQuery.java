package Controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessQuery {
    public static final String CREATE_DATABASE_QUERY =
            "create database";
    public static final String CREATE_DATABASE_QUERY_STRING =
            "^(?i)(CREATE\\sDATABASE\\s[a-zA-Z\\d]+;)$";
    public static final String USE_DATABASE_QUERY =
            "use database";
    public static final String USE_DATABASE_QUERY_STRING =
            "^(?i)(USE\\sDATABASE\\s[a-zA-Z\\d]+;)$";
    private String useDatabaseName= "";
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
        return true;
    }
    private String executeCreateDatabaseQuery(String query){
        String dbName = query.substring(0,query.length()-1).split(" ")[2];
        System.out.println(dbName);
        String path ="./src/main/java/Model/database/"+ dbName;
        File databasePath = new File(path);
        databasePath.mkdirs();
        return "CREATED DB SUCCESSFULLY !!!";
    }

    private String executeUseDatabaseQuery(String query){
        String dbName = query.substring(0,query.length()-1).split(" ")[2];
        final File[] files = new File("./src/main/java/Model/database/").listFiles();
        for (final File file : files) {
            if (file.getName().equalsIgnoreCase(dbName)) {
                this.useDatabaseName = file.getName();
            }
        }
        return this.useDatabaseName+ " HAS BEEN SELECTED SUCCESSFULLY !!!";
    }
    private String executeCreateTableQuery(String query) throws Exception {
        String tableName = query.substring(0,query.length()-1).split(" ")[2];
        String path ="./src/main/java/Model/database/"+ this.useDatabaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = "./src/main/java/Model/database/" + this.useDatabaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();

        final Pattern pattern = Pattern.compile(CREATE_TABLE_COLUMN_STRING);
        final Matcher matcher = pattern.matcher(query);
        if (matcher.find()) {
            final String[] columnNames = matcher.group().split(",");
            try (final FileWriter fileWriter = new FileWriter(tablePath+tableName+".txt")) {
                final StringBuilder createStringBuilder = new StringBuilder();
                for (final String columnToken : columnNames) {
                    final String[] tokens = columnToken.trim().split(" ");
                    if (tokens.length == 2) {
                        createStringBuilder.append(tokens[0])
                                .append("(").append(tokens[1])
                                .append(")")
                                .append("$||$");
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("PRIMARY")) {
                        createStringBuilder.append(tokens[0])
                                .append("(").append(tokens[1]).append("|")
                                .append("PK")
                                .append(")")
                                .append("$||$");
                    }
                    if (tokens.length == 4 && tokens[2].equalsIgnoreCase("REFERENCES")) {
                        final String foreignKeyTable = tokens[3].split("\\(")[0];
                        String foreignKeyCol = tokens[3].split("\\(")[1].replaceAll("\\)", "");
                        createStringBuilder.append(tokens[0]).append("(")
                                .append(tokens[1]).append("|")
                                .append("FK").append("|")
                                .append(foreignKeyTable).append("|")
                                .append(foreignKeyCol)
                                .append(")")
                                .append("$||$");
                    }
                }
                createStringBuilder.replace(createStringBuilder.length() - "$||$".length(), createStringBuilder.length(), "");
                createStringBuilder.append("\n");
                fileWriter.append(createStringBuilder.toString());
            }
            catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        return "TABLE HAS BEEN CREATED SUCCESSFULLY !!!";
    }

    private String executeInsertQuery(String query) throws Exception {

        String tableName = query.substring(0,query.length()-1).split(" ")[2];
//        System.out.println(tableName);
        String path ="./src/main/java/Model/database/"+ this.useDatabaseName;
        File databasePath = new File(path);
        if(!databasePath.isDirectory()){
            throw new Exception("DATABASE doesn't exist");
        }
        final String tablePath = "./src/main/java/Model/database/" + this.useDatabaseName + "/";
        final File allTablesPath = new File(tablePath);
        final File[] allTables = allTablesPath.listFiles();
        final Pattern pattern = Pattern.compile(INSERT_COLUMN_NAME_STRING);
        final Matcher matcher = pattern.matcher(query);
        final Pattern pattern1 = Pattern.compile(INSERT_VALUES_STRING);
        final Matcher matcher1 = pattern1.matcher(query);
        if(matcher.find()){
            final String allColumnNames = matcher.group();
            final String[] columnNamesArray = allColumnNames.replace(")", "").split(",");
            final Set<String> columnNames = new HashSet<>(Arrays.asList(columnNamesArray));
            final int numberOfColumns = columnNames.size();
            if (matcher1.find()) {
                final String allColumnValues = matcher1.group().substring(8, matcher1.group().length() - 1);
                final String[] columnValues = allColumnValues.replace("\"", "").split(",");
                final Map<String, String> columnValue = new LinkedHashMap<>();
                for (int i = 0; i < numberOfColumns; i++) {
                    columnValue.put(columnNamesArray[i].trim(), columnValues[i].trim());
                }
                try (final FileWriter fileWriter = new FileWriter(tablePath+tableName+".txt"
                        , true);
                     final BufferedReader bufferedReader = new BufferedReader(new
                             FileReader(tablePath+tableName+".txt"))) {
                    final String columnNamesInFile = bufferedReader.readLine();
                    final String[] columnNamesExtracted = columnNamesInFile.split("\\$\\|\\|\\$");
                    final LinkedHashMap<String, String> columnDetails = new LinkedHashMap<>();
                    for (final String column : columnNamesExtracted) {
                        final String[] temporaryTokens = column.replace(")", "").split("\\(");
                        columnDetails.put(temporaryTokens[0].replace("(", ""), temporaryTokens[1].split("\\|")[0]);
                    }
                    for (int i = 0; i < columnNamesExtracted.length; i++) {
                        String[] temporaryTokens = columnNamesExtracted[i].replace(")", "").split("\\(");
                    }
                    final StringBuilder valueStringBuilder = new StringBuilder();
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
                        valueStringBuilder.append(column_value).append("$||$");
                    }
                    valueStringBuilder.replace(valueStringBuilder.length() - "$||$".length(),
                            valueStringBuilder.length(), "");
                    valueStringBuilder.append("\n");
                    fileWriter.append(valueStringBuilder.toString());
                }
            }
        }
        return "RECORD HAS BEEN INSERTED SUCCESSFULLY !!!";
    }
}
