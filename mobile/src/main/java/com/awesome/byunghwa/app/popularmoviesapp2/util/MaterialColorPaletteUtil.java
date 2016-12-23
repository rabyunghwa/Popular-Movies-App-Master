package com.awesome.byunghwa.app.popularmoviesapp2.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

import com.awesome.byunghwa.app.popularmoviesapp2.R;


/**
 * Created by ByungHwa on 12/15/2015.
 */
public class MaterialColorPaletteUtil {

    public static int fetchPrimaryDarkColor(Context context) {

        int color = 0;
        if (context != null) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
            color = a.getColor(0, 0);

            a.recycle();
        }

        return color;
    }

    public static int fetchPrimaryColor(Context context) {
        int color = 0;
        if (context != null) {
            TypedValue typedValue = new TypedValue();

            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
            color = a.getColor(0, 0);

            a.recycle();
        }

        return color;
    }

    public static int fetchAccentColor(Context context) {
        int color = 0;
        if (context != null) {
            TypedValue typedValue = new TypedValue();

            TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
            color = a.getColor(0, 0);

            a.recycle();
        }

        return color;
    }

}
