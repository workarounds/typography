package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 06/09/15.
 */
public class AppCompatButton extends android.support.v7.widget.AppCompatButton{
    public AppCompatButton(Context context) {
        super(context);
    }

    public AppCompatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public AppCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.getInstance(getContext()).setTypography(this, attrs);
    }
}
