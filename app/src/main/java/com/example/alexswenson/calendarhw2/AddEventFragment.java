package com.example.alexswenson.calendarhw2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private EditText title_editText;
    private EditText date_editText;
    private EditText time_editText;
    private Button cancel_button;
    private Button save_button;

    private Calendar calendar;

    public AddEventFragment() {
        // Required empty public constructor
    }

    public static AddEventFragment newInstance() {
        AddEventFragment fragment = new AddEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        calendar = Calendar.getInstance();

        title_editText = (EditText) view.findViewById(R.id.title_editText);
        date_editText = (EditText) view.findViewById(R.id.date_editText);
        time_editText = (EditText) view.findViewById(R.id.time_editText);

        cancel_button = (Button) view.findViewById(R.id.cancel_button);
        save_button = (Button) view.findViewById(R.id.save_button);

        setDateOnEditText();
        setTimeOnEditText();

        date_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new DatePickerDialog(view.getContext(), date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        time_editText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new TimePickerDialog(view.getContext(), time,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false).show();
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        save_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if ("".equals(title_editText.getText().toString())) {
                    new AlertDialog.Builder(getActivity())
                            .setMessage("Please provide a title for the event.")
                            .setCancelable(true)
                            .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();

                    return;
                }

                Intent data = new Intent();
                data.putExtra("title", title_editText.getText().toString());
                data.putExtra("date", calendar.getTime());

                // Activity finished OK, return the data
                getActivity().setResult(getActivity().RESULT_OK, data);
                getActivity().finish();
            }
        });

        return view;
    }

    private DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setDateOnEditText();
        }
    };

    private TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            setTimeOnEditText();
        }
    };

    private void setDateOnEditText() {
        String dateFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);

        date_editText.setText(sdf.format(calendar.getTime()));
    }

    private void setTimeOnEditText() {
        String timeFormat = "h:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.US);

        time_editText.setText(sdf.format(calendar.getTime()));
    }

}
