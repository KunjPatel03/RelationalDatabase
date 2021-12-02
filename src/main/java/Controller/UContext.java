package Controller;

public class UContext {
    static String currentDb ;

    public static void setDatabase(String databaseName)
    {
        currentDb = databaseName;
    }
    public static String getCurrentDb()
    {
        return currentDb;
    }
}
