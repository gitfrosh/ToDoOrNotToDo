package de.ueberdiespree.todoornottodov02;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    public static final String LOGGER = "ULRIKE";
    public SqlHandler sqlHandler;
    public ListView lvCustomList;
    public Button btnsubmit, btnrefresh;
    public ArrayList<Item> list = new ArrayList<Item>();
    public ArrayList<String> items_id = new ArrayList<String>();
    public ArrayList<String> array_server_ids = new ArrayList<String>();
    public ListAdapter listAdapter;
    public Item listItem;
    public Item serverItem;
    public String add_name_str, add_descr_str, add_done_str, add_fav_str;
    Cursor c1;
    int rowPosition;
    String ip = "http://10.0.3.2/";
    BufferedReader in = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        //zunächst mal Connection zum Server checken und Warnhinweis einblenden, falls nicht--------/

        try {
            if (isOnline()) {
                Log.d(LOGGER, "Connection fine.");
            } else {
                Toast.makeText(MainActivity.this,
                        "NO CONNECTION TO SERVER!", Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-----------------------------------------------------------------------------------------/

        //hier wird das Layout aufgebaut: Liste, Buttons

        Log.d(LOGGER, "Load View and Layout..");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvCustomList = (ListView) findViewById(R.id.lv_custom_list);

        btnsubmit = (Button) findViewById(R.id.btn_submit);
        btnrefresh = (Button) findViewById(R.id.btn_refresh);
        sqlHandler = new SqlHandler(this);

        //Wichtig! Liste (Adapter) wird geladen
        showList();

        //------------------------------Button ClickListener----------------------------------------
        btnrefresh.setOnClickListener(new OnClickListener() {


            @Override
            public void onClick(View v) {
                showList();
            }
        });

        //------------------------------Button ClickListener----------------------------------------
        btnsubmit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                final Context context = view.getContext();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View formElementsView = inflater.inflate(R.layout.todo_add_form, null, false);

                final DatePicker dpResult = (DatePicker) formElementsView.findViewById(R.id.datePicker);
                final TimePicker tpResult = (TimePicker) formElementsView.findViewById(R.id.timePicker);
                final EditText add_name = (EditText) formElementsView.findViewById(R.id.add_name);
                final EditText add_descr = (EditText) formElementsView.findViewById(R.id.add_descr);

                final CheckBox add_fav = (CheckBox) formElementsView.findViewById(R.id.checkFav);

                new AlertDialog.Builder(context)
                        .setView(formElementsView)
                        .setTitle("Add Item")
                        .setPositiveButton("Add",
                                //Dialog, id -> ist eine Abkürzung!, löst einen neuen Listener für einen
                                //Dialog aus, beim Klick darauf werden die Texteingaben ausgewertet und in
                                //Angaben in das neue Objekt geschrieben

                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        add_name_str = add_name.getText().toString();
                                        add_descr_str = add_descr.getText().toString();
                                        add_done_str = "false"; //wird ein Item angelegt, ist
                                        //es immer erst unerledigt


                                        add_fav_str = "";
                                        if (add_fav.isChecked()) {
                                            add_fav_str = "true";
                                        } else {
                                            add_fav_str = "false";
                                        }


                                        Calendar cal = new GregorianCalendar(dpResult.getYear(),
                                                dpResult.getMonth(),
                                                dpResult.getDayOfMonth(),
                                                tpResult.getCurrentHour(),
                                                tpResult.getCurrentMinute());


                                        long dateInLong = cal.getTimeInMillis();
                                        //Kalenderdaten in long umwandeln

                                        int zzahl = new Random().nextInt(10000) + 1;
                                        //Zufalls-ID erstellen

                                        //Item aus "Add"-Dialog in SQLite schreiben
                                        SQLiteCRUD insertItem = new SQLiteCRUD(sqlHandler, "");
                                        insertItem.insertItemIntoSQLite(zzahl, add_name_str, add_fav_str,
                                                add_done_str, dateInLong, add_descr_str);


                                        //erstelle ServerItem, zur Vorbereitung auf HttpTask
                                        serverItem = new Item(new SQLiteCRUD(sqlHandler, "").findLatestItem(),
                                                add_name_str, add_descr_str, add_fav_str, add_done_str,
                                                dateInLong);

                                        ////////////////////////////////////////////////////////////

                                        new HttpAsyncTask(serverItem,
                                                "http://10.0.3.2:8080/api/todos", "POST").execute();

                                        showList();

                                        dialog.cancel();

                                    }

                                }).show();

            }
        });

    }


    //------------------------------is Online?------------------------------------------------------

    public boolean isOnline() throws InterruptedException, IOException
    {
        String command = "ping -c 1 10.0.3.2"; //localhost anpingen um Konnektivität zu checken
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }


    //------------------------------show List-------------------------------------------------------

    public void showList() {

        Log.d(LOGGER, "Show List..");

        list.clear();

        //Der Array, in dem sich nur die Item-IDs befinden, wird geleert, um ihn jedes Mal, wenn
        //gelöscht/editiert wird, neu aufzubauen
        items_id.clear();

        //erst mal alle Items aus SQLite lesen und in Array schreiben...
        SQLiteCRUD selectItems = new SQLiteCRUD(sqlHandler, "");
        c1 = selectItems.selectItemsFromSQLite();
        if (c1 != null && c1.getCount() != 0) {
            if (c1.moveToFirst()) {
                do {
                    int newId = c1.getInt(0);
                    //Log.d(LOGGER, "newID " + newId);
                    listItem = new Item(c1.getInt(c1
                            .getColumnIndex("todo_id")),c1.getString(c1
                            .getColumnIndex("todo_name")), c1.getString(c1
                            .getColumnIndex("todo_descr")), c1.getString(c1
                            .getColumnIndex("todo_fav")), c1.getString(c1
                            .getColumnIndex("todo_done")), Long.parseLong(c1.getString(c1
                            .getColumnIndex("todo_date"))));

                    //füge Item zum Array hinzu
                    list.add(listItem);

                    //füge ItemIDs zum extra angelegten Array hinzu
                    items_id.add("" + newId);


                } while (c1.moveToNext());
            }
        }

        // call AsynTask to perform network operation on separate thread

        try {

            //JSON Array erstellen mit JSON-Objekten, die vom Server geholt wurden
            Log.d(LOGGER, "result from GET" + new HttpAsyncTask(null,
                    null, "GET").execute().get());

            JSONArray jArr = new JSONArray(new HttpAsyncTask(null,
                    null, "GET").execute().get());

            //für alle Objekte im JSON Array...
            for (int i = 0; i < jArr.length(); ++i) {

                JSONObject jObj = jArr.getJSONObject(i);
                String inta = new Integer(jObj.getInt("id")).toString();
                //Server-ID grabben


                if (!checkArray(items_id, inta)) {
                    // Server-ID ist noch nicht in SQlite-DB vorhanden, also dort neu,
                    // muss auch auf Gerät angelegt werden

                    listItem = new Item(jObj.getInt("id"), jObj.getString("name"), jObj.getString(
                            "description"), jObj.getString("favourite"), jObj.getString("done"),
                            jObj.getLong("expiry"));

                    list.add(listItem);
                    items_id.add(jObj.getString("id"));

                    ///////////////////////////////////////////////////////////
                    Log.d(LOGGER, "Server-ID wurde nicht in SQlite-IDs gefunden, daher wird" +
                            " Item in SQliteDB erstellt");
                    SQLiteCRUD insertItem = new SQLiteCRUD(sqlHandler, "");
                    insertItem.insertItemIntoSQLite(jObj.getInt("id"), jObj.getString("name")
                            , jObj.getString("favourite"), jObj.getString("done"),
                            jObj.getLong("expiry"), jObj.getString("description"));
                }
            }

            //für alle SQLite-Items..
            for (int i = 0; i < items_id.size(); ++i) {

                String inta = items_id.get(i);
                //SQLite-ID grabben

                array_server_ids.clear();
                //Helfer-Array bereinigen

                for (int x = 0; x < jArr.length(); ++x) {
                    //für alle JSON-Objekte..

                    JSONObject jObj = jArr.getJSONObject(x);
                    array_server_ids.add(jObj.getString("id"));
                    //zum Helfer-Array Server-IDs hinzufügen

                }

                //wenn SQlite-ID nicht mehr im Helfer-Server-ID-Array enthalten ist UND,
                //Server-ID-Array nicht leer ist
                //lösche Item in SQlite

                if (!checkArrayServer(array_server_ids, inta))  {


                    String delQuery = "DELETE FROM TO_DOS WHERE todo_id='" + inta + "' ";
                    sqlHandler.executeQuery(delQuery);

                    Log.d(LOGGER, "Item aus SQLite-DB ist nicht auf Server vorhanden, daher wird es aus SQlite geslöscht" +
                            "mit Query:= " + delQuery);

                    //wenn Server leer ist, übertrage alle Item aus SQLite zum Server
                } //else if (array_server_ids.isEmpty())  {

                //Log.d(LOGGER, "alle Item zum Server!");

                //String query = "INSERT INTO TO_DOS(todo_id, todo_name,todo_fav,todo_done,todo_date,todo_descr) values ('"
                //        + jObj.getInt("id") + "','" + jObj.getString("name") + "','" + jObj.getString("favourite") + "','" + jObj.getString("done") + "','" + jObj.getLong("expiry") + "','" + add_descr_str + "')";
                //sqlHandler.executeQuery(query);
                //Log.d(LOGGER, "Query= " + query);




            }


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        listAdapter = new List_Adapter(
                MainActivity.this, list);

        //unsortierter Adapter
        lvCustomList.setAdapter(listAdapter);
        Log.d(LOGGER, "Erstelle Adapter...");

        //sortierter Adapter
        //lvCustomList.setAdapter(listAdapter.sortData());


        registerForContextMenu(lvCustomList);
        //Log.d(LOGGER, "items_id " + items_id);


        lvCustomList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent intent = new Intent(getApplicationContext(), DetailviewActivity.class);

                //Log.d(LOGGER, "position " + position);


                long intentId = Long.valueOf(items_id.get(position));

                //Log.d(LOGGER, " tempID " + intentId);

                intent.putExtra("ID", intentId);

                startActivity(intent);
            }
        });




    }



    //ContextMenü bei langem Klick------------------------------------------------------------------
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        final long id = info.id;
        rowPosition = info.position; //gleicher Effekt wie ID:

        switch (item.getItemId()) {
            case R.id.edit:
                editFav();

                return true;
            case R.id.delete:
                Log.d(LOGGER, "Löschen geklickt");

                String id_string = items_id.get(rowPosition);

                //Log.d(LOGGER, "ID " + id + ", position " + rowPosition + ", id_string " + id_string);
                long tempId = Long.valueOf(id_string);

                //Item SQLite löschen
                SQLiteCRUD deleteItem = new SQLiteCRUD(sqlHandler, "");
                deleteItem.deleteItemFromSQLite(tempId);


//          then you probably want it off the corresponding collection too
                items_id.remove(rowPosition);


                ////////////////////////////////////////////////////////////
                //Lösche auf Server////////////////////////////////////////

                String url = "http://10.0.3.2:8080/api/todos/" + tempId;
                Log.d(LOGGER, "Lösche Item vom Server mit URL: " + url);

                new HttpAsyncTask(serverItem,
                        url, "DELETE").execute();

                ////////////////////////////////////////////////////////////


                showList();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    //ContextMenü "Edit"---------------------------------------------------------------------------

    private void editFav() {
        Log.d(LOGGER, "Edit geklickt");
        final CheckBox edit_fav;
        final CheckBox edit_done;

        final Context context = this;


        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_item_form);
        dialog.setTitle("Bearbeiten...");

        // set the custom dialog components - text, image and button

        edit_fav = (CheckBox) dialog.findViewById(R.id.star_edit);
        edit_done = (CheckBox) dialog.findViewById(R.id.done_edit);


        Button dialogButton = (Button) dialog.findViewById(R.id.buttonEDIT);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String favEDIT;
                String doneEDIT;

                String id_string = items_id.get(rowPosition);
                long tempId = Long.valueOf(id_string);
                //Log.d(LOGGER, "id_string " + id_string + " tempID " + tempId);

                if (edit_fav.isChecked()) {
                    favEDIT = "true";
                } else {
                    favEDIT = "false";
                }

                if (edit_done.isChecked()) {
                    doneEDIT = "true";
                } else {
                    doneEDIT = "false";
                }


                //Log.d(LOGGER, doneEDIT + favEDIT);

                SQLiteCRUD updateItem = new SQLiteCRUD(sqlHandler, "");
                updateItem.updateItemInSQLite(favEDIT, doneEDIT, tempId);


                //erstelle ServerItem, zur Vorbereitung auf HttpTask
                serverItem = new Item(((int) (long) tempId),
                        listItem.getName(), listItem.getDescription(), favEDIT, doneEDIT,
                        listItem.getExpiry());

                ////////////////////////////////////////////////////////////
                //Update auf Server////////////////////////////////////////

                String url = "http://10.0.3.2:8080/api/todos/" + tempId;
                Log.d(LOGGER, "Editiere Item auf Server mit URL: " + url);

                new HttpAsyncTask(serverItem,
                        url, "PUT").execute();

                ////////////////////////////////////////////////////////////


                showList();
                dialog.dismiss();

            }
        });
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_one:


                Collections.sort(list, new Comparator<Item>() {
                    @Override
                    public int compare(Item lhs, Item rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });


                Log.d(LOGGER, "" + list);
                break;

            case R.id.action_two:
                Log.d(LOGGER, "" + list);
                break;
        }
        return true;

    }


    public boolean checkArray(ArrayList<String> items_id, String b) {
        //checkt ob eine ID vom Server schon als SQlite-ID vorhanden ist
        return items_id.contains(b);

    }

    public boolean checkArrayServer(ArrayList<String> array_server_ids, String c) {
        //checkt ob eine ID aus SQLItite noch auf Server vorhanden ist
        return array_server_ids.contains(c);

    }

    public void synchronize(String result) {



    }
}
