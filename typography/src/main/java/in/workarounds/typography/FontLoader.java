package in.workarounds.typography;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by madki on 22/08/15.
 */
public class FontLoader {
    private static String TAG = "FontLoader";
    private static String SEPARATOR = "-";
    private static String FONT_ROOT = "fonts";

    private static FontLoader mFontLoader;
    private Context mContext;
    private String[] mFontFiles;
    private String mDefaultFontName;
    private String mDefaultFontVariant;
    private HashMap<String, Typeface> mTypefaces;

    public static FontLoader getInstance(Context context) {
        if (mFontLoader == null) {
            mFontLoader = new FontLoader(context);
        }
        return mFontLoader;
    }

    private FontLoader(Context context) {
        mContext = context;
        mTypefaces = new HashMap<>();

        TypedValue fontNameValue = new TypedValue();
        TypedValue fontVariantValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.defaultFontName, fontNameValue, true);
        context.getTheme().resolveAttribute(R.attr.defaultFontVariant, fontVariantValue, true);
        String defaultFontName = (String) fontNameValue.string;
        String defaultFontVariant = (String) fontVariantValue.string;
        Log.d(TAG, "FontLoader defaultFontName" + defaultFontName);
        Log.d(TAG, "FontLoader defaultFontVariant" + defaultFontVariant);
        setDefaults(defaultFontName, defaultFontVariant);

        try {
            mFontFiles = mContext.getAssets().list(FONT_ROOT);
        } catch (IOException e) {
            Log.e(TAG, "No fonts folder found in assets");
            e.printStackTrace();
        }
    }

    public Typeface getTypeface(String fontName, String fontVariant) {
        Log.d(TAG, "getTypeface fontName: " + fontName + " fontVariant: " + fontVariant);
        if(TextUtils.isEmpty(fontName)) {
            fontName = mDefaultFontName;
        }
        if(TextUtils.isEmpty(fontVariant)) {
            fontVariant = mDefaultFontVariant;
        }

        String hash = getHash(fontName, fontVariant);
        if (mTypefaces.containsKey(hash)) {
            return mTypefaces.get(hash);
        } else {
            return getNewTypeface(fontName, fontVariant, hash);
        }
    }

    public Typeface getTypeface(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        String fontName, fontVariant;
        try{
            fontName = a.getString(R.styleable.TextView_font_name);
            fontVariant = a.getString(R.styleable.TextView_font_variant);
        } finally{
            a.recycle();
        }

        return getTypeface(fontName, fontVariant);
    }

    public void setTypeface(android.widget.TextView textView, AttributeSet attrs){
        Typeface typeface = getTypeface(textView.getContext(), attrs);
        textView.setTypeface(typeface);
    }

    public void setTypography(android.widget.TextView textView, AttributeSet attrs){
        if(!textView.isInEditMode()) {
            setTypeface(textView, attrs);
        }
    }

    private String getHash(String fontName, String fontVariant) {
        return fontName + SEPARATOR + fontVariant;
    }

    private Typeface getNewTypeface(String fontName, String fontVariant, String hash) {
        Typeface typeface = null;
        boolean fallback = false;

        String fontFile = getFontFile(fontName, fontVariant);
        if (TextUtils.isEmpty(fontFile) && !TextUtils.isEmpty(fontVariant)) {
            fallback = true;
            fontFile = getFontFile(fontName, null);
        }

        if (!TextUtils.isEmpty(fontVariant)) {
            fontFile = getFontFile(fontName, fontVariant);

            if (TextUtils.isEmpty(fontFile)) {
                fallback = true;
                fontFile = getFontFile(fontName, null);
                Log.e(TAG, "falling back to base font " + fontFile);
            }
        }

        try {
            typeface = Typeface.createFromAsset(mContext.getAssets(), fontFile);
        } catch (RuntimeException e) {
            Log.e(TAG, "Font asset not found " + fontFile);
        }

        if (fallback) {
            typeface = getCorrectedTypeface(typeface, fontVariant);
        }

        mTypefaces.put(hash, typeface);
        return typeface;
    }

    private String getFontFile(String fontName, String fontVariant) {
        if (mFontFiles == null || mFontFiles.length == 0 || TextUtils.isEmpty(fontName)) {
            Log.e(TAG, "No fonts folder in assets");
            return null;
        }

        String fontFile = fontName;
        if (!TextUtils.isEmpty(fontVariant)) {
            fontFile = fontFile + SEPARATOR + fontVariant;
        }

        return searchInFontFiles(fontFile);
    }

    private String searchInFontFiles(String fontFile) {
        for (String file : mFontFiles) {
            if (getFileWithoutExt(file).toLowerCase().equals(fontFile.toLowerCase())) {
                return FONT_ROOT + File.separator + file;
            }
        }
        Log.e(TAG, "failed to find " + fontFile + " in assets/fonts");
        return null;
    }

    private Typeface getCorrectedTypeface(Typeface typeface, String fontVariant) {
        String lowerVariant = fontVariant.toLowerCase();

        if (lowerVariant.equals("bold")) {
            Log.e(TAG, "No bold font found. Making it bold using code. Not advisable.");
            return Typeface.create(typeface, Typeface.BOLD);
        } else if (lowerVariant.equals("italic")) {
            Log.e(TAG, "No italic font found. Making it italic using code. Not advisable.");
            return Typeface.create(typeface, Typeface.ITALIC);
        } else if (lowerVariant.equals("bolditalic")) {
            Log.e(TAG, "No bolditalic font found. Making it bolditalic using code. Not advisable.");
            return Typeface.create(typeface, Typeface.BOLD_ITALIC);
        }

        Log.e(TAG, "Font variant not recognized. Please add font for " + fontVariant);
        return typeface;
    }

    private String getFileWithoutExt(String file) {
        if (!TextUtils.isEmpty(file)) {
            int dotIndex = file.lastIndexOf(".");
            if (dotIndex != -1) {
                return file.substring(0, dotIndex);
            }
        }

        return file;
    }

    public void setDefaults(String fontName, String fontVariant) {
        setDefaultFontName(fontName);
        setDefaultFontVariant(fontVariant);
    }

    public void setDefaultFontName(String fontName) {
        mDefaultFontName = fontName;
    }

    public void setDefaultFontVariant(String fontVariant) {
        mDefaultFontVariant = fontVariant;
    }

}
