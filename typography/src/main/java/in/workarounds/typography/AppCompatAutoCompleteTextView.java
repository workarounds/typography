package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 24/08/15.
 */
public class AppCompatAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public AppCompatAutoCompleteTextView(Context context) {
        super(context);
    }

    public AppCompatAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public AppCompatAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.setTypography(this, attrs);
    }
}
