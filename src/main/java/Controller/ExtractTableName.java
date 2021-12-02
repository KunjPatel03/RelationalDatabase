package Controller;

public class ExtractTableName {

    public String getCreateQueryTableName(String query){
        String tableName = query.substring(0,query.length()-1).split(" ")[2];
        return tableName;
    }

    public String getInsertQueryTableName(String query){
        String tableName = query.substring(0,query.length()-1).split(" ")[2];
        return tableName;
    }

    public String getSelectQueryTableName(String query){
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = queryArray[queryArray.length-1];
        return tableName;
    }

    public String getSelectWithConditionQueryTableName(String query){
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = "";
        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("from")){
                tableName = queryArray[i+1];
            }
        }
        return tableName;
    }

    public String getUpdateWithConditionQueryTableName(String query){
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = "";
        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("update")){
                tableName = queryArray[i+1];
            }
        }
        return tableName;
    }


    public String getDeleteWithConditionQueryTableName(String query){
        String[] queryArray = query.substring(0,query.length()-1).split(" ");
        String tableName = "";

        for(int i =0; i<queryArray.length;i++){
            if(queryArray[i].equalsIgnoreCase("from")){
                tableName = queryArray[i+1];
            }
        }

        return tableName;
    }



}
