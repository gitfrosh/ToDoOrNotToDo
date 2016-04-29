package de.ueberdiespree.todoornottodov02;

/**
 * Created by Ulrike on 16.09.2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqlDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_TABLE = "TO_DOS";

    public static final String LOGGER = "ULRIKE";

    public static final String COLUMN1 = "todo_id";
    public static final String COLUMN2 = "todo_name";
    public static final String COLUMN3 = "todo_fav";
    public static final String COLUMN4 = "todo_done";
    public static final String COLUMN5 = "todo_date";
    public static final String COLUMN6 = "todo_descr";
    public static final String COLUMN7 = "timeStamp";
    private static final String SCRIPT_CREATE_DATABASE = "create table "
            + DATABASE_TABLE + " (" + COLUMN1
            + " int null, " + COLUMN2
            + " text not null, " + COLUMN3
            + " text not null, " + COLUMN4
            + " text not null, " + COLUMN5
            + " long, " + COLUMN6
            + " text not null, " + COLUMN7
            + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

    public SqlDbHelper(Context context, String name, CursorFactory factory,
                       int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SCRIPT_CREATE_DATABASE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        //onCreate(db);

        if (oldVersion != newVersion) {
            Log.d(LOGGER, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ". No data will be destroyed");
            //db.execSQL(ALTER_TABLE);
        }
    }

}