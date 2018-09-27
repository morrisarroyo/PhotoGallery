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

    // IntentsTestRule is an extension of ActivityTestRule. IntentsTestRule sets up Espresso-Intents
    // before each Test is executed to allow stubbing and validation of intents.
    //@Rule
    //public IntentsTestRule<MainActivity> intentsRule = new IntentsTestRule<>(MainActivity.class);
/*
    @Test
    public void ensureFilterDateWork() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_dateFrom))
                .perform(typeText("20180915"), closeSoftKeyboard());
        onView(withId(R.id.editText_dateTo))
                .perform(typeText("20180930"), closeSoftKeyboard());

        onView(withId(R.id.button_filter_search)).perform(click());
        // Check that the text was changed.
        onView(withId(R.id.imageView));

    }
*/
    @Test
    public void ensureFilterCaptionWork() {

        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_captionFilter))
                .perform(typeText("Food"), closeSoftKeyboard());

        onView(withId(R.id.button_filter_caption)).perform(click());
        // Check that the text was changed.
       // onView(withId(R.id.editText_caption)).check(matches(withText("Food")));

    }


    @Test
    public void takePicture() {

        onView(withId(R.id.button_snap)).perform(click());
        onView(withId(R.id.editText_caption)).check(matches(withText("Caption")));
    }

    @Test
    public void ensurePictureNavigationWork() {

        onView(withId(R.id.button_reset_gallery)).perform(click());
        onView(withId(R.id.button_filter)).perform(click());
        onView(withId(R.id.editText_captionFilter))
                .perform(typeText("Caption"), closeSoftKeyboard());
        onView(withId(R.id.button_filter_caption)).perform(click());
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
}
