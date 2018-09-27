package a00950540.bcit.ca.photogallery;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    Calendar myCalendar;
    EditText dateFromText;
    EditText dateToText;
    Button filterButton;
    Date dateFrom;
    Date dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        filterButton = (Button) findViewById(R.id.button_filter_search);
        myCalendar =  Calendar.getInstance();
        dateFromText = (EditText) findViewById(R.id.editText_dateFrom);
        dateToText = (EditText) findViewById(R.id.editText_dateTo);
        filterButton.setOnClickListener(filterListener);

        //dateFromText.setOnClickListener(dateFromListener);
        //dateToText.setOnClickListener(dateToListener);
    }


    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent data = new Intent();

            data.putExtra("dateFrom", dateFromText.getText().toString());
            data.putExtra("dateTo", dateToText.getText().toString());
            setResult(RESULT_OK, data);
            finish();
        }
    };

    DatePickerDialog.OnDateSetListener dateFromPicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateFromLabel();
        }

    };

    DatePickerDialog.OnDateSetListener dateToPicker = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateToLabel();
        }

    };


    private View.OnClickListener dateFromListener = new View.OnClickListener() {
        public void onClick(View v) {
            new DatePickerDialog(SearchActivity.this, dateFromPicker, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    private View.OnClickListener dateToListener = new View.OnClickListener() {
        public void onClick(View v) {
            new DatePickerDialog(SearchActivity.this, dateToPicker, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };
    private void updateDateFromLabel() {
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateFromText.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateDateToLabel() {
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateToText.setText(sdf.format(myCalendar.getTime()));
    }
}
