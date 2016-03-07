package com.example.alexswenson.calendarhw2;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.QueryBuilder;

public class CalendarActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private ListView eventsListView;

    private ArrayList<Event> eventsList;
    private ArrayAdapter eventsListAdapter;

    private DaoMaster.DevOpenHelper calendarDBHelper;
    private SQLiteDatabase calendarDB;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private EventDao eventDao;

    private final int ADD_EVENT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calendarView = (CalendarView) findViewById(R.id.calendar_calendarView);
        eventsListView = (ListView) findViewById(R.id.events_listView);

        eventsList = new ArrayList<>();

        // Setup the eventsListAdapter
        eventsListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        eventsListView.setAdapter(eventsListAdapter);

        // initialise the database
        initDatabase();

        // Populate the eventsList for the date selected on the calendar view
        setEventsListForDate(new Date(calendarView.getDate()));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                setEventsListForDate(new Date(view.getDate()));
            }
        });

        // Set the action of the 'Add' button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), AddEventActivity.class);
                startActivityForResult(intent, ADD_EVENT_REQUEST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendar, menu);
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

    public void setEventsListForDate(Date date) {

        Calendar today = Calendar.getInstance();
        today.setTime(date);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar tomorrow = Calendar.getInstance();
        tomorrow.setTime(date);
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);

        // Get list of Guest objects in database using QueryBuilder.
        // If list is null, then database tables were created for first time,
        // so we call "closeReopenDatabase()" to reopen the database.
        QueryBuilder queryBuilder = eventDao.queryBuilder();
        List<Event> eventListFromDB = queryBuilder.where(EventDao.Properties.Date.between(today.getTime(), tomorrow.getTime())).list();

        if (eventListFromDB == null) {
            closeReopenDatabase();
            eventListFromDB = queryBuilder.where(EventDao.Properties.Date.between(today.getTime(), tomorrow.getTime())).list();
        }

        eventsList.clear();
        eventsList.addAll(eventListFromDB);

        eventsListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Check which request we're responding to and ensure the result was successful
        if (requestCode == ADD_EVENT_REQUEST && resultCode == RESULT_OK) {

            // Generate random Id for Guest object to place in database
            Random rand = new Random();

            // Use rand.nextLong() for Guest object Id.
            long id = rand.nextLong();

            // Make sure the id doesn't already exist in the database
            while (eventDao.load(id) != null) {
                id = rand.nextLong();
            }

            String title = data.getStringExtra("title");
            Date date = (Date) data.getSerializableExtra("date");

            Event event = new Event(id, title, date);

            eventDao.insert(event);

            closeReopenDatabase();

            setEventsListForDate(new Date(calendarView.getDate()));
        }
    }

    private void initDatabase() {
        calendarDBHelper = new DaoMaster.DevOpenHelper(this, "ORM.sqlite", null);
        calendarDB = calendarDBHelper.getWritableDatabase();

        // Get DaoMaster
        daoMaster = new DaoMaster(calendarDB);

        // Create initial database table if they do not exist
        DaoMaster.createAllTables(calendarDB, true);

        // Create a database access session
        daoSession = daoMaster.newSession();

        // Get instance of eventDao
        eventDao = daoSession.getEventDao();
    }

    private void closeDatabase() {
        daoSession.clear();
        calendarDB.close();
        calendarDBHelper.close();
    }

    private void closeReopenDatabase() {
        closeDatabase();

        calendarDBHelper = new DaoMaster.DevOpenHelper(this, "ORM.sqlite", null);
        calendarDB = calendarDBHelper.getWritableDatabase();

        //Get DaoMaster
        daoMaster = new DaoMaster(calendarDB);

        // Create DaoSession instance
        // Use method in DaoMaster to create a database access session
        daoSession = daoMaster.newSession();

        // From DaoSession instance, get instance of eventDao
        eventDao = daoSession.getEventDao();
    }
}
