package in.workarounds.typography;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by mouli on 10/16/15.
 */
public class CustomView  {

    public CustomView(Context context) {

    }

    public CustomView(Context context, AttributeSet attrs) {
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
    }

    public void setTypeface(Typeface typeface) {
    }

    public boolean isInEditMode() {
        return true;
    }

    public final Context getContext() {
        return new Activity().getBaseContext();
    }
}
