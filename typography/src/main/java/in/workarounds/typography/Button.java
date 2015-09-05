package in.workarounds.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * Created by madki on 06/09/15.
 */
public class Button extends android.widget.Button {
    private String mFontName;
    private String mFontVariant;

    public Button(Context context) {
        super(context);
        setFont();
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        setFont();
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        setFont();
    }

    private void getAttrs(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.EditText);
        try{
            mFontName = a.getString(R.styleable.Button_font_name);
            mFontVariant = a.getString(R.styleable.Button_font_variant);
        } finally{
            a.recycle();
        }
    }

    private void setFont() {
        if(isInEditMode()) {
            return;
        }

        Typeface typeface = FontLoader.getInstance(getContext()).getTypeface(mFontName, mFontVariant);
        setTypeface(typeface);
    }
}
