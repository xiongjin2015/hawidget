
package com.haha.hwidget.util;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * 屏幕相关的Utils
 * 
 * @author xj
 */
public class HaScreen {

    private static DisplayMetrics getDisplayMetrics(Context ctx) {
        return ctx.getApplicationContext().getResources().getDisplayMetrics();
    }

    /**
     * 获得设备的dpi
     */
    public static float getScreenDpi(Context ctx) {
        return getDisplayMetrics(ctx).densityDpi;
    }

    /**
     * 获得设备屏幕密度
     */
    public static float getScreenDensity(Context ctx) {
        DisplayMetrics dm = getDisplayMetrics(ctx);
        return dm.density;
    }

    /**
     * 获得设备屏幕密度
     */
    public static float getScreenScaledDensity(Context ctx) {
        DisplayMetrics dm = getDisplayMetrics(ctx);
        return dm.scaledDensity;
    }

    /**
     * 获得设备屏幕宽度
     */
    public static int getScreenWidth(Context ctx) {
        DisplayMetrics dm = getDisplayMetrics(ctx);
        return dm.widthPixels;
    }

    /**
     * 获得设备屏幕高度 According to phone resolution height
     */
    public static int getScreenHeight(Context ctx) {
        DisplayMetrics dm = getDisplayMetrics(ctx);
        return dm.heightPixels;
    }

    /**
     * According to the resolution of the phone from the dp unit will become a px (pixels)
     */
    public static int dip2px(Context ctx, int dip) {
        float density = getScreenDensity(ctx);
        return (int) (dip * density + 0.5f);
    }

    /**
     * Turn from the units of px (pixels) become dp according to phone resolution
     */
    public static int px2dip(Context ctx, float px) {
        float density = getScreenDensity(ctx);
        return (int) (px / density + 0.5f);
    }

    /**
     * Turn from the units of px (pixels) become sp according to phone scaledDensity
     * 
     * @param ctx
     * @param px
     * @return
     */
    public static int px2sp(Context ctx, float px) {
        float scale = getScreenScaledDensity(ctx);
        return (int) (px / scale + 0.5f);
    }

    /**
     * According to the scaledDensity of the phone from the sp unit will become a px (pixels)
     * 
     * @param ctx
     * @param sp
     * @return
     */
    public static int sp2px(Context ctx, int sp) {
        float scale = getScreenScaledDensity(ctx);
        return (int) (sp * scale + 0.5f);
    }
}
