package a00950540.bcit.ca.photogallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;
    public static final int SEARCH_CAPTION_ACTIVITY_REQUEST_CODE = 2;

    static final int CAMERA_REQUEST_CODE = 1;
    private String currentPhotoPath = null;
    private int currentPhotoIndex = 0;
    private ArrayList<String> photoGallery;
    EditText et;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLeft      = (Button)findViewById(R.id.button_previous);
        Button btnRight     = (Button)findViewById(R.id.button_next);
        Button btnFilter    = (Button)findViewById(R.id.button_filter);
        Button btnSnap      = (Button)findViewById(R.id.button_snap);
        Button btnSave      = (Button)findViewById(R.id.button_save);
        Button btnReset      = (Button)findViewById(R.id.button_reset_gallery);
        et = (EditText) findViewById(R.id.editText_caption);

        btnLeft.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnFilter.setOnClickListener(filterListener);
        btnSnap.setOnClickListener(snapListener);
        btnSave.setOnClickListener(saveListener);
        btnReset.setOnClickListener(resetListener);

        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(minDate, maxDate);
        Log.d("onCreate, size", Integer.toString(photoGallery.size()));
        if (photoGallery.size() > 0)
            currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);
        et.setText(getCaption(currentPhotoPath));
    }


    private View.OnClickListener filterListener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent i = new Intent(MainActivity.this, SearchCaptionActivity.class);
            startActivityForResult(i, SEARCH_CAPTION_ACTIVITY_REQUEST_CODE);
        }
    };

    private View.OnClickListener snapListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_snap", "button_snap onclick");
            takePicture(v);
        }
    };

    private View.OnClickListener saveListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_save", "button_save onclick");
            EditText et = (EditText) findViewById(R.id.editText_caption);
            renamePhoto(et.getText().toString().trim());
        }
    };

    private View.OnClickListener resetListener = new View.OnClickListener() {
        public void onClick(View v) {
            Log.d("button_reset", "button_reset onclick");
            resetGallery();
        }
    };

    private void resetGallery()
    {
        Date dateFrom = new Date(Long.MIN_VALUE);
        Date dateTo = new Date(Long.MAX_VALUE);
        photoGallery = populateGallery(dateFrom, dateTo);
        currentPhotoIndex = 0;
        currentPhotoPath = photoGallery.get(currentPhotoIndex);
        displayPhoto(currentPhotoPath);
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/a00950540.bcit.ca.photogallery/files/Pictures");
        photoGallery = new ArrayList<String>();
        String myFormat = "yyyyMMdd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        File[] fList = file.listFiles();


        if (fList != null) {
            for (File f : file.listFiles()) {
/*
                String dateStr = "";
                String[] split = f.getPath().split("/");
                split = split[split.length - 1].split("_");
                dateStr = split[2];
                Date date;
                try {
                    date = sdf.parse(dateStr);
                } catch (ParseException pe) {
                    date = new Date(Long.MIN_VALUE);
                }
                if (date.after(minDate) || date.before(maxDate)) {
*/
                    photoGallery.add(f.getPath());
               // }
            }
        }
        Collections.reverse(photoGallery);
        return photoGallery;
    }
    private void displayPhoto(String path) {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(BitmapFactory.decodeFile(path));

        EditText et = (EditText) findViewById(R.id.editText_caption);
        et.setText(getCaption(path));
    }

    private void renamePhoto(String caption) {
        String renameString = "";
        File image = new File(currentPhotoPath);
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        String[] split = currentPhotoPath.split("/");
        Log.d("renamePhoto",split[split.length - 1] );
        split = split[split.length - 1].split("_");
        split[1] = caption;
        Log.d("renamePhoto", "Caption: " + caption);
        for (int i = 1; i < split.length; ++i) {
            renameString += "_" + split[i];
        }

        File renameImage = new File(dir,  renameString);
        if (image.renameTo(renameImage)) {
            photoGallery.set(currentPhotoIndex, renameImage.getAbsolutePath());
            currentPhotoPath = renameImage.getAbsolutePath();
        } else
        {
            Log.d("renamePhoto", "Rename Failed");
        }
    }

    private String getCaption(String path) {
        //File image = File.createTempFile(imageFileName, ".jpg", dir );
        String caption = "";
        String[] split = path.split("/");
        split = split[split.length - 1].split("_");
        caption = split[1];
        Log.d("getCaption", caption);
        return caption;
    }

    private ArrayList<String> filterGalleryByCaption(String caption, ArrayList<String> gallery) {
        Log.d("filterGalleryByCaption", "Enter");
        Date dateFrom = new Date(Long.MIN_VALUE);
        Date dateTo = new Date(Long.MAX_VALUE);
        gallery = populateGallery(dateFrom, dateTo);
        Iterator<String> itr = gallery.iterator();
        while(itr.hasNext()){
            String next = itr.next();
            if (!getCaption(next).equals(caption)) {
                itr.remove();
            }
        }
        Log.d("filterGalleryByCaption", "Exit");

        return gallery;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onClick( View v) {
        switch (v.getId()) {
            case R.id.button_previous:
                --currentPhotoIndex;
                Log.d("button_previous"," Previous Image");
                break;
            case R.id.button_next:
                ++currentPhotoIndex;
                Log.d("button_next"," Next Image");
                break;
            default:
                break;
        }

        if (currentPhotoIndex < 0)
            currentPhotoIndex = 0;
        if (currentPhotoIndex >= photoGallery.size())
            currentPhotoIndex = photoGallery.size() - 1;

        currentPhotoPath = photoGallery.get(currentPhotoIndex);

        Log.d("photosave, size", getCaption(currentPhotoPath));
        Log.d("photoleft, size", Integer.toString(photoGallery.size()));
        Log.d("photoleft, index", Integer.toString(currentPhotoIndex));
        displayPhoto(currentPhotoPath);
    }


    public void goToSettings(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void goToDisplay(String x) {
        Intent i = new Intent(this, DisplayActivity.class);
        i.putExtra("DISPLAY_TEXT", x);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", data.getStringExtra("dateFrom"));
                Log.d("createImageFile", data.getStringExtra("dateTo"));
                String myFormat = "yyyyMMdd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                Date dateFrom;
                Date dateTo;
                try {
                    dateFrom = sdf.parse(data.getStringExtra("dateFrom"));
                } catch (ParseException pe) {
                    dateFrom = new Date(Long.MIN_VALUE);
                }
                try {
                    dateTo = sdf.parse(data.getStringExtra("dateTo"));
                } catch (ParseException pe) {
                    dateTo = new Date(Long.MAX_VALUE);
                }
                photoGallery = populateGallery(dateFrom, dateTo);
                Log.d("onCreate, size", Integer.toString(photoGallery.size()));
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
        if (requestCode == SEARCH_CAPTION_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String caption = data.getStringExtra("caption");
                Date dateFrom = new Date(Long.MIN_VALUE);
                Date dateTo = new Date(Long.MAX_VALUE);
                photoGallery = populateGallery(dateFrom, dateTo);
                photoGallery = filterGalleryByCaption(caption, photoGallery);
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("createImageFile", "Picture Taken");
                photoGallery.add(0, currentPhotoPath);
                currentPhotoIndex = 0;
                currentPhotoPath = photoGallery.get(currentPhotoIndex);
                displayPhoto(currentPhotoPath);
            }
        }
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                Log.d("TryPhotoFile", "Attempt");
                photoFile = createImageFile();
                Log.d("TryPhotoFile", "Succeed");
                Log.d("TryPhotoFile", photoFile.getAbsolutePath());
            } catch (IOException ex) {
                Log.d("FileCreation", "Failed");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "a00950540.bcit.ca.photogallery.fileprovider",
                        photoFile);
                Log.d("PhotoUri", "Succeed");

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "_Caption_" + timeStamp + "_";
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", dir );
        currentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile", image.getAbsolutePath());
        return image;
    }
}
