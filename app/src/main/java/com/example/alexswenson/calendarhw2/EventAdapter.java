package com.example.alexswenson.calendarhw2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by AlexanderSwenson on 3/6/16.
 */
public class EventAdapter extends BaseAdapter {

    private ArrayList<Event> eventsList;
    private Context context;

    private static class ViewHolder {
        TextView title_textView;
        TextView date_textView;
        TextView time_textView;
        Button delete_button;
    }

    public EventAdapter(Context context, ArrayList<Event> eventsList) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public Event getItem(int position) {
        return eventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view

        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.event_list_item, parent, false);

            // Initialize the UI elements
            viewHolder.title_textView = (TextView) convertView.findViewById(R.id.title_textView);
            viewHolder.date_textView = (TextView) convertView.findViewById(R.id.date_textView);
            viewHolder.time_textView = (TextView) convertView.findViewById(R.id.time_textView);
            viewHolder.delete_button = (Button) convertView.findViewById(R.id.delete_button);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        final Event event = getItem(position);

        // Populate the data into the template view using the data object
        viewHolder.title_textView.setText(event.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy", Locale.US);
        viewHolder.date_textView.setText(dateFormat.format(event.getDate()));

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.US);
        viewHolder.time_textView.setText(timeFormat.format(event.getDate()));

        viewHolder.delete_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (context instanceof CalendarActivity) {
                    ((CalendarActivity) context).deleteEvent(event);
                } else {
                    Log.d("EventAdapter", "Event was unable to be deleted. " +
                            "Context not an instance of CalendarActivity.");
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}
