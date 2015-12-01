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
    private String[] mFontFiles;
    private String mDefaultFontName;
    private String mDefaultFontVariant;
    private HashMap<String, Typeface> mTypefaces;

    private static FontLoader getInstance(Context context) {
        if (mFontLoader == null) {
            mFontLoader = new FontLoader(context);
        } else {
            mFontLoader.initFontFiles(context);
        }
        return mFontLoader;
    }

    private static FontLoader getInstance(String defaultFontName, String defaultFontVariant) {
        if(mFontLoader == null) {
            mFontLoader = new FontLoader(defaultFontName, defaultFontVariant);
        } else {
            mFontLoader.setDefaults(defaultFontName, defaultFontVariant);
        }
        return mFontLoader;
    }

    private FontLoader(Context context) {
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

        initFontFiles(context);
    }

    private FontLoader(String defaultFontName, String defaultFontVariant) {
        setDefaults(defaultFontName, defaultFontVariant);
    }

    public static void clear() {
        mFontLoader = null;
    }

    public static void setDefaultFont(String defaultFontName, String defaultFontVariant) {
        getInstance(defaultFontName, defaultFontVariant);
    }

    public static Typeface getTypeface(Context context, String fontName, String fontVariant) {
        Log.d(TAG, "getTypeface fontName: " + fontName + " fontVariant: " + fontVariant);
        FontLoader fontLoader = getInstance(context);
        if(TextUtils.isEmpty(fontName)) {
            fontName = fontLoader.mDefaultFontName;
        }
        if(TextUtils.isEmpty(fontVariant)) {
            fontVariant = fontLoader.mDefaultFontVariant;
        }

        String hash = getHash(fontName, fontVariant);
        if (fontLoader.mTypefaces.containsKey(hash)) {
            return fontLoader.mTypefaces.get(hash);
        } else {
            return fontLoader.getNewTypeface(context, fontName, fontVariant, hash);
        }
    }

    public static Typeface getTypeface(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextView);
        String fontName, fontVariant;
        try{
            fontName = a.getString(R.styleable.TextView_font_name);
            fontVariant = a.getString(R.styleable.TextView_font_variant);
        } finally{
            a.recycle();
        }

        return getTypeface(context, fontName, fontVariant);
    }

    public static void setTypeface(android.widget.TextView textView, AttributeSet attrs){
        Typeface typeface = getTypeface(textView.getContext(), attrs);
        textView.setTypeface(typeface);
    }

    public static void setTypography(android.widget.TextView textView, AttributeSet attrs){
        if(!textView.isInEditMode()) {
            setTypeface(textView, attrs);
        }
    }

    private static String getHash(String fontName, String fontVariant) {
        return fontName + SEPARATOR + fontVariant;
    }

    private Typeface getNewTypeface(Context context, String fontName, String fontVariant, String hash) {
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
            typeface = Typeface.createFromAsset(context.getAssets(), fontFile);
        } catch (RuntimeException e) {
            Log.e(TAG, "Font asset not found " + fontFile);
        }

        if (fallback) {
            typeface = getCorrectedTypeface(typeface, fontVariant);
        }

        mTypefaces.put(hash, typeface);
        return typeface;
    }

    private void initFontFiles(Context context) {
        if(mFontFiles == null) {
            try {
                mFontFiles = context.getAssets().list(FONT_ROOT);
            } catch (IOException e) {
                Log.e(TAG, "No fonts folder found in assets");
                e.printStackTrace();
            }
        }
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

    private void setDefaults(String fontName, String fontVariant) {
        setDefaultFontName(fontName);
        setDefaultFontVariant(fontVariant);
    }

    private void setDefaultFontName(String fontName) {
        mDefaultFontName = fontName;
    }

    private void setDefaultFontVariant(String fontVariant) {
        mDefaultFontVariant = fontVariant;
    }

}
