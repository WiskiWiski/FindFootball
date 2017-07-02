package online.findfootball.android.game.football.screen.my;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import online.findfootball.android.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Create_Game_map {

    @Rule
    public ActivityTestRule<MyGamesActivity> mActivityTestRule = new ActivityTestRule<>(MyGamesActivity.class);

    @Test
    public void create_Game_map() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.google_sign_in_btn), isDisplayed()));
        appCompatImageButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3596737);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        withParent(withId(R.id.toolbar)),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatCheckedTextView = onView(
                allOf(withId(R.id.design_menu_item_text), withText("Create Game"), isDisplayed()));
        appCompatCheckedTextView.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3594479);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.edit_text), isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.edit_text), isDisplayed()));
        appCompatEditText2.perform(replaceText("Sample game"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.edit_text), withText("Sample game"), isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.edit_text), isDisplayed()));
        appCompatEditText4.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.edit_text), isDisplayed()));
        appCompatEditText5.perform(replaceText("Black black black bla"), closeSoftKeyboard());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.edit_text), withText("Black black black bla"), isDisplayed()));
        appCompatEditText6.perform(pressImeActionButton());

        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.right_btn),
                        withParent(withId(R.id.bottom_bar)),
                        isDisplayed()));
        frameLayout.perform(click());

        ViewInteraction frameLayout2 = onView(
                allOf(withId(R.id.right_btn),
                        withParent(withId(R.id.bottom_bar)),
                        isDisplayed()));
        frameLayout2.perform(click());

        ViewInteraction customSpinner = onView(
                allOf(withId(R.id.day_spinner), isDisplayed()));
        customSpinner.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.custom_spinner_item), withText("Other"), isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withClassName(is("android.support.v7.widget.AppCompatImageButton")), withContentDescription("Следующий месяц"),
                        withParent(allOf(withClassName(is("android.widget.DayPickerView")),
                                withParent(withClassName(is("com.android.internal.widget.DialogViewAnimator")))))));
        appCompatImageButton3.perform(scrollTo(), click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("ОК")));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction frameLayout3 = onView(
                allOf(withId(R.id.right_btn),
                        withParent(withId(R.id.bottom_bar)),
                        isDisplayed()));
        frameLayout3.perform(click());

    }

}
