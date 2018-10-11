package a00950540.bcit.ca.photogallery;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class FilterActivity extends AppCompatActivity {

    public static final int DATE_STRING_LENGTH = 8;
    public static final int DATE_TIME_STRING_LENGTH = 14;

    Button filterButton;

    EditText captionEditText;
    EditText dateFromEditText;
    EditText dateToEditText;
    EditText latitudeEditText;
    EditText longitudeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        filterButton        = (Button) findViewById(R.id.button_filter_filter);
        captionEditText     = (EditText) findViewById(R.id.editText_caption_filter);
        dateFromEditText    = (EditText) findViewById(R.id.editText_dateFrom_filter);
        dateToEditText      = (EditText) findViewById(R.id.editText_dateTo_filter);
        latitudeEditText    = (EditText) findViewById(R.id.editText_latitude_filter);
        longitudeEditText   = (EditText) findViewById(R.id.editText_longitude_filter);

        filterButton.setOnClickListener(filterListener);
    }


    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("filterListener", "Enter");
            String dateFromStr     = dateFromEditText.getText().toString().trim();
            String dateToStr       = dateToEditText.getText().toString().trim();
            String latitudeStr     = latitudeEditText.getText().toString().trim();
            String longitudeStr    = longitudeEditText.getText().toString().trim();


            Intent data         = new Intent();

            data.putExtra("caption", captionEditText.getText().toString().trim());

            if (validateDateString(dateFromStr))
                data.putExtra("dateFrom", dateFromStr);
            else
                data.putExtra("dateFrom", "");
            if (validateDateString(dateToStr))
                data.putExtra("dateTo", dateToStr);
            else
                data.putExtra("dateTo", "");

            data.putExtra("latitude", latitudeStr);
            data.putExtra("longitude", longitudeStr);
            setResult(RESULT_OK, data);
            Log.d("filterListener", "Exit");
            finish();
        }
    };

    private boolean validateDateString(String date) {
        String myFormat;
        if (date.length() == DATE_STRING_LENGTH) {
             myFormat = "yyyyMMdd"; //In which you need put here
        } else if (date.length() == DATE_TIME_STRING_LENGTH){
            myFormat = "yyyyMMddHHmmss"; //In which you need put here
        } else {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }
}
