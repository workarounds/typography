package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 22/08/15.
 */
public class AppCompatTextView extends android.support.v7.widget.AppCompatTextView{
    public AppCompatTextView(Context context) {
        super(context);
    }

    public AppCompatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public AppCompatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.setTypography(this, attrs);
    }
}
