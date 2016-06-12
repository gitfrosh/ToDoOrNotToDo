
package de.ueberdiespree.todoornottodov02;
/**
 * Created by Ulrike on 16.09.2015.
 */
/**
 * Created by Ulrike on 11.09.2015.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class List_Adapter extends BaseAdapter {
    public static final String LOGGER = "ULRIKE";
    Context context;
    ArrayList<Item> list;
    Item listItem;
    TextView row_id;
    TextView row_name;
    TextView row_date;
    ImageView row_done;
    ImageView row_fav;

    public List_Adapter(Context context, ArrayList<Item> list2) {

        this.context = context;
        list = list2;
    }

    @Override
    public int getCount() {

        return list.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }



    public void sortDataDone()
    {
        // nicht implementiert
    }




    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        listItem = list.get(position);



        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row1, null);

        }
        row_id = (TextView) convertView.findViewById(R.id.row_id);
        row_id.setText(Integer.toString((listItem.getId())));


        row_name = (TextView) convertView.findViewById(R.id.row_name);
        row_name.setText(listItem.getName());

        //Datum wurde als long gespeichert -> Umwandlung in Date -> Umwandlung in String
        Date d = new Date(listItem.getExpiry());
        String s = d.toString();


        row_date = (TextView) convertView.findViewById(R.id.row_date);
        row_date.setText(s);

        row_done = (ImageView) convertView.findViewById(R.id.check);

        if (listItem.getDone().equals ("true")) {
            row_done.setImageResource(R.drawable.check_true);
        }

        if (listItem.getDone().equals ("false")) {
            row_done.setImageResource(R.drawable.check_false);
        }

        row_fav = (ImageView) convertView.findViewById(R.id.star);

        if (listItem.getFavourite().equals ("true")) {
            row_fav.setImageResource(R.drawable.star_true);
        }

        if (listItem.getFavourite().equals ("false")) {
            row_fav.setImageResource(R.drawable.star_false);
        }

        //check Datum - überfällig?-----------------------------------------------------------------

        //Datum und Zeit aktuell
        Calendar c = Calendar.getInstance();
        long now = c.getTimeInMillis();

        //Check ob aktuelles Datum und Item-Datum imselben Format

        //Convert to Date
        Date formattedNowDate = new Date(now);
        Date formattedItemDate = new Date(listItem.getExpiry());


        //Vergleiche Daten und färbe Zeile rot, wenn Itemdatum vor heutigem Datum liegt
        if (formattedItemDate.before(formattedNowDate)){
            convertView.setBackgroundColor(Color.rgb(255, 204, 204));
        }
        return convertView;
    }


}