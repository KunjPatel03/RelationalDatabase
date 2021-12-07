package Controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

public class AnalyticsController {

    private static final String queryLogsPath = "./src/main/java/Logs/QueryLogs.txt";
    private static final String COUNT_QUERIES =
            "count queries";
    private static final String COUNT_QUERY_STRING =
            "^(?i)(COUNT\\sQUERIES\\s[a-zA-Z\\d]+;)$";
    private static final String COUNT_UPDATE =
            "count update";
    private static final String COUNT_UPDATE_STRING =
            "^(?i)(COUNT\\sUPDATE\\s[a-zA-Z\\d]+;)$";
    private static int queryCount;
    private static int updateCount;

    public String analytics(String query) throws Exception {
        String message = null;
        if (isQueryValid(query)) {
            if (Pattern.matches(COUNT_QUERY_STRING, query)) {
                message = executeCountQuery(query);
            } else if (Pattern.matches(COUNT_UPDATE_STRING, query)) {
                message = executeUpdateQuery(query);
            }
        } else {
            message = "Invalid Analytics query";
        }
        return message;
    }

    private boolean isQueryValid(String query) throws Exception {
        final String Query = query.trim().toLowerCase();
        if (Query.contains(COUNT_QUERIES)) {
            if (!Pattern.matches(COUNT_QUERY_STRING, Query)) {
                return false;
            }
        } else if (Query.contains(COUNT_UPDATE)) {
            if (!Pattern.matches(COUNT_UPDATE_STRING, Query)) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }


    private String executeCountQuery(String query) throws Exception {
        String dbName = query.substring(0, query.length() - 1).split(" ")[2];
        String line, message;
        try (final FileReader fileReader = new FileReader(queryLogsPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("|")) {
                    String dbNameFromLog = line.split("\\|")[2].trim().split(" ")[0];
                    if (dbNameFromLog.equalsIgnoreCase(dbName)) {
                        ++queryCount;
                    }
                }
            }
            message = "Submitted " + queryCount + " queries on " + dbName;
        }
        System.out.println(message);
        return "Analytics executed";
    }

    private String executeUpdateQuery(String query) throws Exception {
        String dbName = query.substring(0, query.length() - 1).split(" ")[2];
        String line, message;
        try (final FileReader fileReader = new FileReader(queryLogsPath);
             final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("|") && line.contains("UPDATE")) {
                    String dbNameFromLog = line.split("\\$")[1].trim().split(" ")[0];
                    if (dbNameFromLog.equalsIgnoreCase(dbName)) {
                        ++updateCount;
                    }
                }
            }
            message = "Total " + updateCount + " Update operations are performed " + dbName;
        }
        System.out.println(message);
        return "Analytics executed";
    }

}
