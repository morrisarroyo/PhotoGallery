package a00950540.bcit.ca.photogallery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SearchCaptionActivity extends AppCompatActivity {
    EditText captionEditText;
    Button filterButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_caption);
        filterButton = (Button) findViewById(R.id.button_filter_caption);
        captionEditText = (EditText) findViewById(R.id.editText_captionFilter);
        filterButton.setOnClickListener(filterListener);
    }

    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("filterListener", "Enter");
            Intent data = new Intent();

            data.putExtra("caption", captionEditText.getText().toString().trim());
            setResult(RESULT_OK, data);
            Log.d("filterListener", "Exit");
            finish();
        }
    };
}
