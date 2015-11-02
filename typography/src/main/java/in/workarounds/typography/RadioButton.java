package in.workarounds.typography;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by madki on 13/10/15.
 */
public class RadioButton extends android.widget.RadioButton {
    public RadioButton(Context context) {
        super(context);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypography(attrs);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypography(attrs);
    }

    private void setTypography(AttributeSet attrs) {
        FontLoader.getInstance(getContext()).setTypography(this, attrs);
    }
}
