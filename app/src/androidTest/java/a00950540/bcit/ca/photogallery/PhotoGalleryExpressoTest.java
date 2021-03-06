package a00950540.bcit.ca.photogallery;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.KeyEvent;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runner.RunWith;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressKey;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class PhotoGalleryExpressoTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ensureFilterCaptionWork() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_caption_filter))
                .perform(typeText("Food"), closeSoftKeyboard());

        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.editText_caption)).check(matches(withText("Food")));

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
        onView(withId(R.id.button_reset_gallery)).perform(click());
        onView(withId(R.id.editText_caption)).perform(clearText())
                .perform(typeText("Banana"), closeSoftKeyboard());
        onView(withId(R.id.button_save)).perform(click());
        onView(withId(R.id.button_next)).perform(click());
        onView(withId(R.id.button_previous)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Banana")));
    }


/*
    @Test
    public void takePicture() {
        onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Caption")));
    }
*/
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

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_latitude_filter))
                .perform(typeText("49.2804"), closeSoftKeyboard());
        onView(withId(R.id.editText_longitude_filter))
                .perform(typeText("-122.9704"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.text_value_latitude)).check(matches(withText("49.280483")));
        onView(withId(R.id.text_value_longitude)).check(matches(withText("-122.97044")));
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

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_dateFrom_filter))
                .perform(typeText("20181010170000"), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo_filter))
                .perform(typeText("20181010171000"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.text_value_date_gallery)).check(matches(withText("20181010170610")));
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

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_dateFrom_filter))
                .perform(typeText("20181007212000"), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo_filter))
                .perform(typeText("20181007212200"), closeSoftKeyboard());
        onView(withId(R.id.editText_latitude_filter))
                .perform(typeText("49.2804"), closeSoftKeyboard());
        onView(withId(R.id.editText_longitude_filter))
                .perform(typeText("-122.9704"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_filter)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.text_value_date_gallery)).check(matches(withText("20181007212144")));
        onView(withId(R.id.text_value_latitude)).check(matches(withText("49.280483")));
        onView(withId(R.id.text_value_longitude)).check(matches(withText("-122.97044")));
        onView(withId(R.id.text_value_index_gallery)).check(matches(withText("0")));
    }


}
