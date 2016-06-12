package de.ueberdiespree.todoornottodov02;

import android.database.Cursor;
import android.util.Log;

/**
 * Created by ulrike on 12.04.16.
 */
public class SQLiteCRUD {

    private static final String LOGGER = "ULRIKE";
    public SqlHandler sqlHandler;
    String query;

    public SQLiteCRUD(SqlHandler sqlHandler, String query) {
        this.sqlHandler = sqlHandler;
        this.query = query;
    }

    public void insertItemIntoSQLite(int zzahl, String add_name_str, String add_fav_str,
                                     String add_done_str, long dateInLong, String add_descr_str) {
        query = "INSERT INTO TO_DOS(todo_id, todo_name,todo_fav,todo_done,todo_date," +
                "todo_descr) values ('" + zzahl + "','" + add_name_str + "','" + add_fav_str + "','"
                + add_done_str + "','" + dateInLong + "','" + add_descr_str + "')";
        sqlHandler.executeQuery(query);
        Log.d(LOGGER, "Erstelle Item mit Query:= " + query);

    }

    public int findLatestItem() {

        String queryfindlatest = "SELECT * from TO_DOS ORDER BY timeStamp DESC limit 1";
        Cursor c = sqlHandler.selectQuery(queryfindlatest);
        int lastId;
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0); //The 0 is the column index, we only have 1 column, so the index is 0

        } else {
            lastId = 0;
        }

        return lastId;
    }

    public void deleteItemFromSQLite(long tempId) {

        query = "DELETE FROM TO_DOS WHERE todo_id='" + tempId + "' ";
        sqlHandler.executeQuery(query);
        Log.d(LOGGER, "Lösche Item aus SQlite-DB mit Query: " + query);

    }

    public Cursor selectItemsFromSQLite() {

        String query = "SELECT * FROM TO_DOS ";
        Cursor c1 = sqlHandler.selectQuery(query);

        return c1;
    }

    public void updateItemInSQLite(String favEDIT, String doneEDIT, long tempId) {

        String upquery = "UPDATE TO_DOS SET todo_fav='" + favEDIT + "',todo_done='" + doneEDIT + "' WHERE todo_id=" + tempId;
        Log.d(LOGGER, "Editiere Item in SQlite-DB mit Query: " + upquery);
        sqlHandler.executeQuery(upquery);
    }

    public void updateItemInSQLite(String str, int identifier, long j) {

        switch (identifier) {
            case 1:

                String upquery = "UPDATE TO_DOS SET todo_name='" + str + "' WHERE todo_id=" + j;
                Log.d(LOGGER, "Editiere Item in SQlite-DB mit Query: " + upquery);
                sqlHandler.executeQuery(upquery);

                break;

            case 2:

                upquery = "UPDATE TO_DOS SET todo_descr='" + str + "' WHERE todo_id=" + j;
                Log.d(LOGGER, "Editiere Item in SQlite-DB mit Query: " + upquery);
                sqlHandler.executeQuery(upquery);

                break;
        }


    }

    public void updateItemInSQLite(long dateInLong, long j) {




    }
}