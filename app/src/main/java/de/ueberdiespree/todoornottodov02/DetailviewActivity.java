package de.ueberdiespree.todoornottodov02;


/**
 * Created by Ulrike on 16.09.2015.
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DetailviewActivity extends AppCompatActivity {
    public static final String LOGGER = "ULRIKE";
    public ArrayList<Item> list = new ArrayList<Item>();
    public Button btndelete;
    public Button btnedit;
    public Button btneditname;
    public Button btneditdescr;
    public Button btneditdate;
    public Button btnaddcontact;
    SqlHandler sqlHandler;
    Cursor c1;
    TextView tv;
    Item listItem;
    long j;
    Item serverItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailview);
        sqlHandler = new SqlHandler(this);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();


        if(b!=null)
        {
            j =(long) b.get("ID");
            //Log.d(LOGGER, "" + j);
            String query = "SELECT * FROM TO_DOS WHERE todo_id='" + j + "' ";
            c1 = sqlHandler.selectQuery(query);
            Log.d(LOGGER, "Selektiere Item aus SQlite-DB mit Query: " + query);


            if (c1 != null && c1.getCount() != 0) {
                if (c1.moveToFirst()) {

                    //int newId = c1.getInt(0);

                    listItem = new Item();

                    listItem.setId(c1.getInt(c1
                            .getColumnIndex("todo_id")));

                    listItem.setExpiry(c1.getLong(c1
                            .getColumnIndex("todo_date")));

                    listItem.setDescription(c1.getString(c1
                            .getColumnIndex("todo_descr")));

                    listItem.setDone(c1.getString(c1
                            .getColumnIndex("todo_done")));

                    listItem.setFavourite(c1.getString(c1
                            .getColumnIndex("todo_fav")));

                    listItem.setName(c1.getString(c1
                            .getColumnIndex("todo_name")));

                    list.add(listItem);



                }
            }


            TextView row_name = (TextView) findViewById(R.id.detail_name);
            row_name.setText(listItem.getName());

            //Datum wurde als long gespeichert -> Umwandlung in Date -> Umwandlung in String
            Date d = new Date(listItem.getExpiry());
            String s = d.toString();

            TextView row_date = (TextView) findViewById(R.id.detail_date);
            row_date.setText(s);

            TextView row_descr = (TextView) findViewById(R.id.detail_descr);
            row_descr.setText(listItem.getDescription());

            ImageView row_done = (ImageView) findViewById(R.id.detail_check);

            if (listItem.getDone().equals ("true")) {
                row_done.setImageResource(R.drawable.check_true);
            }

            ImageView row_fav = (ImageView) findViewById(R.id.detail_star);

            if (listItem.getFavourite().equals ("true")) {
                row_fav.setImageResource(R.drawable.star_true);
            }

            btndelete = (Button) findViewById(R.id.btn_delete_detail);

            btndelete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    alertMessageDelete();

                    Log.d(LOGGER, "Löschen geklickt");

                }


            });

            btneditdescr = (Button) findViewById(R.id.btn_edit_descr);

            btneditdescr.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    itemEditDescr();

                    Log.d(LOGGER, "Beschreibung Editieren geklickt");

                }


            });

            btneditname = (Button) findViewById(R.id.btn_edit_name);

            btneditname.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    itemEditName();

                    Log.d(LOGGER, "Name Editieren geklickt");

                }


            });

            btneditdate = (Button) findViewById(R.id.btn_edit_date);

            btneditdate.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    itemEditDate();

                    Log.d(LOGGER, "Date Editieren geklickt");

                }


            });

            btnedit = (Button) findViewById(R.id.btn_edit_detail);

            btnedit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    itemEditFavDone();

                    Log.d(LOGGER, "Fav/Prio Editieren geklickt");

                }


            });

            // man müsste hier noch die Kontakte implementieren..
            /*btnaddcontact = (Button) findViewById(R.id.btn_add_contact);

            btnaddcontact.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    addContact();

                    Log.d(LOGGER, "Kontakt soll hinzugefügt werden");

                }


            });*/

        }


    }

    private void itemEditDate() {

        Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_item_date);
        dialog.setTitle("Bearbeiten...");

        // set the custom dialog components - text, image and button

        final DatePicker dpResult = (DatePicker) dialog.findViewById(R.id.datePicker);
        final TimePicker tpResult = (TimePicker) dialog.findViewById(R.id.timePicker);
        final Button dialogButton = (Button) dialog.findViewById(R.id.buttonEDIT);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = new GregorianCalendar(dpResult.getYear(),
                        dpResult.getMonth(),
                        dpResult.getDayOfMonth(),
                        tpResult.getCurrentHour(),
                        tpResult.getCurrentMinute());


                long dateInLong = cal.getTimeInMillis();
                //Kalenderdaten in long umwandeln

                SQLiteCRUD updateItem = new SQLiteCRUD(sqlHandler, "");
                updateItem.updateItemInSQLite(dateInLong, j);

                serverItem = new Item();
                serverItem.setId((int) (long) j);
                serverItem.setName(listItem.getName());
                serverItem.setDescription(listItem.getDescription());
                serverItem.setFavourite(listItem.getFavourite());
                serverItem.setDone(listItem.getDone());
                serverItem.setExpiry(dateInLong);

                ////////////////////////////////////////////////////////////
                //Update auf Server////////////////////////////////////////

                String url = "http://10.0.3.2:8080/api/todos/" + j;
                Log.d(LOGGER, "Editiere Item auf Server mit URL: " + url);

                new HttpAsyncTask(serverItem,
                        url, "PUT").execute();

                ////////////////////////////////////////////////////////////

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        dialog.show();
    }


    private void itemEditDescr() {

        Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_item_descr);
        dialog.setTitle("Bearbeiten...");

        // set the custom dialog components - text, image and button

        final Button dialogButton = (Button) dialog.findViewById(R.id.buttonEDIT);
        final EditText edit_descr = (EditText) dialog.findViewById(R.id.edit_descr);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String edit_descr_str = edit_descr.getText().toString();

                int identifier = 0;


                if (edit_descr_str.isEmpty()){

                    dialog.cancel();

                } else {

                    identifier = 2; // Identifier 1: Beschreibung wird editiert

                    SQLiteCRUD updateItem = new SQLiteCRUD(sqlHandler, "");
                    updateItem.updateItemInSQLite(edit_descr_str, identifier, j);

                    serverItem = new Item();
                    serverItem.setId((int) (long) j);
                    serverItem.setName(listItem.getName());
                    serverItem.setDescription(edit_descr_str);
                    serverItem.setFavourite(listItem.getFavourite());
                    serverItem.setDone(listItem.getDone());
                    serverItem.setExpiry(listItem.getExpiry());

                    ////////////////////////////////////////////////////////////
                    //Update auf Server////////////////////////////////////////

                    String url = "http://10.0.3.2:8080/api/todos/" + j;
                    Log.d(LOGGER, "Editiere Item auf Server mit URL: " + url);

                    new HttpAsyncTask(serverItem,
                            url, "PUT").execute();

                    ////////////////////////////////////////////////////////////

                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        dialog.show();
    }


    private void addContact() {

        //Intent selectContactOntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        //startActivityForResult(selectContactIntent, 1);
    }


    private void itemEditName() {

        Context context = this;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.edit_item_name);
        dialog.setTitle("Bearbeiten...");

        // set the custom dialog components - text, image and button

        final Button dialogButton = (Button) dialog.findViewById(R.id.buttonEDIT);
        final EditText edit_name = (EditText) dialog.findViewById(R.id.edit_name);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(LOGGER, "listItem.getName()" +  listItem.getName());

                String edit_name_str = edit_name.getText().toString();

                int identifier = 0;


                if (edit_name_str.isEmpty()){

                    dialog.cancel();

                } else {

                    identifier = 1; // Identifier 1: Name wird editiert

                    SQLiteCRUD updateItem = new SQLiteCRUD(sqlHandler, "");
                    updateItem.updateItemInSQLite(edit_name_str, identifier, j);

                    serverItem = new Item();
                    serverItem.setId((int) (long) j);
                    serverItem.setName(edit_name_str);
                    serverItem.setDescription(listItem.getDescription());
                    serverItem.setFavourite(listItem.getFavourite());
                    serverItem.setDone(listItem.getDone());
                    serverItem.setExpiry(listItem.getExpiry());

                    ////////////////////////////////////////////////////////////
                    //Update auf Server////////////////////////////////////////

                    String url = "http://10.0.3.2:8080/api/todos/" + j;
                    Log.d(LOGGER, "Editiere Item auf Server mit URL: " + url);

                    new HttpAsyncTask(serverItem,
                            url, "PUT").execute();

                    ////////////////////////////////////////////////////////////

                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        dialog.show();



    }




    private void itemEditFavDone() {

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
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String favEDIT;
                String doneEDIT;

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

                Log.d(LOGGER, "listItem.getName()" +  listItem.getName());

                String upquery = "UPDATE TO_DOS SET todo_fav='" + favEDIT + "',todo_done='" + doneEDIT + "' WHERE todo_id=" + j;

                Log.d(LOGGER, "Editiere Item in SQlite-DB mit Query: " + upquery);
                sqlHandler.executeQuery(upquery);


                serverItem = new Item();
                serverItem.setId((int) (long) j);
                serverItem.setName(listItem.getName());
                serverItem.setDescription(listItem.getDescription());
                serverItem.setFavourite(favEDIT);
                serverItem.setDone(doneEDIT);
                serverItem.setExpiry(listItem.getExpiry());

                Log.d(LOGGER, "serverItem.getName()" + serverItem.getName());

                ////////////////////////////////////////////////////////////
                //Update auf Server////////////////////////////////////////

                String url = "http://10.0.3.2:8080/api/todos/" + j;
                Log.d(LOGGER, "Editiere Item auf Server mit URL: " + url);

                new HttpAsyncTask(serverItem,
                        url, "PUT").execute();

                ////////////////////////////////////////////////////////////

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);

            }
        });
        dialog.show();



    }

    private void alertMessageDelete() {


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            public void onClick(
                    DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE: // Yes button clicked


                        //Item SQLite löschen
                        SQLiteCRUD deleteItem = new SQLiteCRUD(sqlHandler, "");
                        deleteItem.deleteItemFromSQLite(j);


                        //items_id.remove(rowPosition);


                        ////////////////////////////////////////////////////////////
                        //Lösche auf Server////////////////////////////////////////

                        String url = "http://10.0.3.2:8080/api/todos/" + j;
                        Log.d(LOGGER, "Lösche Item auf Server mit URL: " + url);

                        new HttpAsyncTask(serverItem,
                                url, "DELETE").execute();

                        ////////////////////////////////////////////////////////////


                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);

                        break;
                    case DialogInterface.BUTTON_NEGATIVE: // No button clicked // do nothing

                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}