package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 22/08/15.
 */
public class TextView extends android.widget.TextView{
    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.getInstance(getContext()).setTypography(this, attrs);
    }
}
