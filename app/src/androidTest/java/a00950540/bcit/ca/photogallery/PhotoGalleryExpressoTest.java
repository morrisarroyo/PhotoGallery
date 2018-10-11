package a00950540.bcit.ca.photogallery;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.base.MainThread;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class PhotoGalleryExpressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);
/*
    private Instrumentation.ActivityResult getSubCameraIntent() {
        Intent resultData = new Intent();
        Bitmap bp = BitmapFactory.decodeResource(mIntentsRule.getActivity().getResources(),
                R.mipmap.ic_launcher);
        Bundle bundle = new Bundle();
        bundle.putParcelable("0", bp);
        resultData.putExtras(bundle);
        Instrumentation.ActivityResult result =
                new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        return result;
    }
    */
    @Before
    public void populateGalleryWithDummyImages() {
        File dir = mActivityRule.getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File[] files = dir.listFiles();
        for(File file: files) {
            file.delete();
        }
        for(int i = 0; i < 10; i++ ) {
            String imageFileName = "_Caption" + ""
                    + "_" + "2018100" + i
                    + "_" + "11110" + i + "_.jpg";
            File image = new File(dir, imageFileName);

            Context c = mActivityRule.getActivity().getApplicationContext();
            //Drawable d = c.getDrawable();
            Bitmap bp = BitmapFactory.decodeResource(c.getResources(), R.mipmap.setsuna);
            try {
                FileOutputStream out = new FileOutputStream(image);
                bp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
            } catch (IOException ex) {
                //Log.d("FileCreation", "Failed");
            }
        }
    }

    @Test
    public void ensureFilterCaptionWork() {

        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_caption_filter))
                .perform(typeText("Food"), closeSoftKeyboard());

        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        //onView(withId(R.id.editText_caption)).check(matches(withText("Food")));
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));

    }

    @Test
    public void ensureFilterCaptionInvalid() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_caption_filter))
                .perform(typeText("Kvothe"), closeSoftKeyboard());

        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.editText_caption)).check(matches(withText("No Image Found")));

    }


    @Test
    public void ensurePictureNavigationWork() {

        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_reset_gallery)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_caption_filter))
                .perform(typeText("Caption"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        onView(withId(R.id.button_next)).perform(click());
        onView(withId(R.id.button_previous)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Caption")));
    }

    @Test
    public void ensureSaveCaptionWork() {

        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_reset_gallery)).perform(click());
        onView(withId(R.id.editText_caption)).perform(clearText())
                .perform(typeText("Felurian"), closeSoftKeyboard());
        onView(withId(R.id.button_save)).perform(click());
        onView(withId(R.id.button_next)).perform(click());
        onView(withId(R.id.button_previous)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Felurian")));
    }


    @Test
    public void takePicture() {
        onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Caption")));
    }
    
    @Test
    public void ensureFilterNothingWork() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_caption_filter));
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));
    }

    @Test
    public void ensureFilterLocationWork() {
        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_latitude_filter))
                .perform(typeText("49.2804"), closeSoftKeyboard());
        onView(withId(R.id.editText_longitude_filter))
                .perform(typeText("-122.9704"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        //onView(withId(R.id.text_value_latitude)).check(matches(withText("49.280483")));
        //onView(withId(R.id.text_value_longitude)).check(matches(withText("-122.97044")));
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));
    }

    @Test
    public void ensureFilterLocationInvalid() {
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_latitude_filter))
                .perform(typeText("51"), closeSoftKeyboard());
        onView(withId(R.id.editText_longitude_filter))
                .perform(typeText("-140"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.editText_caption)).check(matches(withText("No Image Found")));
    }
    @Test
    public void ensureFilterDateTimeWork() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        String todayAsString = dateFormat.format(today);
        String tomorrowAsString = dateFormat.format(tomorrow);

        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_dateFrom_filter))
                .perform(typeText(todayAsString), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo_filter))
                .perform(typeText(tomorrowAsString), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        //onView(withId(R.id.text_value_date_gallery)).check(matches(withText("20181010170610")));
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));
    }

    @Test
    public void ensureFilterDateTimeInvalid() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_dateFrom_filter))
                .perform(typeText("19900000000000"), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo_filter))
                .perform(typeText("19900000000001"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.editText_caption)).check(matches(withText("No Image Found")));
    }


    @Test
    public void ensureFilterWork() {
        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();

        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        String todayAsString = dateFormat.format(today);
        String tomorrowAsString = dateFormat.format(tomorrow);

        //onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());

        onView(withId(R.id.editText_caption_filter))
                .perform(typeText("Caption"), closeSoftKeyboard());
        onView(withId(R.id.editText_dateFrom_filter))
                .perform(typeText(todayAsString), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo_filter))
                .perform(typeText(tomorrowAsString), closeSoftKeyboard());
        onView(withId(R.id.editText_latitude_filter))
                .perform(typeText("49.2804"), closeSoftKeyboard());
        onView(withId(R.id.editText_longitude_filter))
                .perform(typeText("-122.9704"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        //onView(withId(R.id.text_value_date_gallery)).check(matches(withText("20181007212144")));
        //onView(withId(R.id.text_value_latitude)).check(matches(withText("49.280483")));
        //onView(withId(R.id.text_value_longitude)).check(matches(withText("-122.97044")));
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));
    }


}
