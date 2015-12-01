package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 23/08/15.
 */
public class AppCompatEditText extends android.support.v7.widget.AppCompatEditText {
    public AppCompatEditText(Context context) {
        super(context);
    }

    public AppCompatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public AppCompatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.setTypography(this, attrs);
    }

}
