package in.workarounds.typography;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;

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
        try {
            mFontFiles = mContext.getAssets().list(FONT_ROOT);
        } catch (IOException e) {
            Log.e(TAG, "No fonts folder found in assets");
            e.printStackTrace();
        }
    }

    public Typeface getTypeface(String fontName, String fontVariant) {
        String hash = getHash(fontName, fontVariant);
        if (mTypefaces.containsKey(hash)) {
            return mTypefaces.get(hash);
        } else {
            return getNewTypeface(fontName, fontVariant, hash);
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
        if (mFontFiles == null || mFontFiles.length == 0) {
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

}
