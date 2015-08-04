
package com.haha.hwidget.util;

import java.io.File;

import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

/**
 * @author xj Description: hwidget使用的一些公共utils
 **/
public class Utils {

    public static int getRealVisibility(int visibility, int defaultValue) {
        switch (visibility) {
            case 0x00000000:// attrs:visible
                return View.VISIBLE;
            case 0x00000004:// attrs:invisible
                return View.INVISIBLE;
            case 0x00000008:// attrs:gone
                return View.GONE;
            default:
                return defaultValue;
        }
    }

    public static View measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int paramsHeight = params.height;
        int childHeightSpec;
        if (paramsHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(paramsHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }

        view.measure(childWidthSpec, childHeightSpec);
        return view;
    }

    public static boolean fileExists(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
