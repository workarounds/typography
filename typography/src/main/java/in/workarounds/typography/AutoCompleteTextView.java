package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 24/08/15.
 */
public class AutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public AutoCompleteTextView(Context context) {
        super(context);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs){
        FontLoader.getInstance(getContext()).setTypography(this, attrs);
    }
}
