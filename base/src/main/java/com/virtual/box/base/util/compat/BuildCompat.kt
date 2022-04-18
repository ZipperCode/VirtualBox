package com.virtual.box.base.util.compat

import android.annotation.SuppressLint
import android.os.Build

/**
 * 版本适配类
 */
@SuppressLint("AnnotateVersionCheck")
object BuildCompat {

    // 12
    @JvmStatic
    val isAtLeastS: Boolean
        get() = Build.VERSION.SDK_INT >= 31 || Build.VERSION.SDK_INT >= 30 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 11
    @JvmStatic
    val isAtLeastR: Boolean
        get() = Build.VERSION.SDK_INT >= 30 || Build.VERSION.SDK_INT >= 29 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 10
    @JvmStatic
    val isAtLeastQ: Boolean
        get() = Build.VERSION.SDK_INT >= 29 || Build.VERSION.SDK_INT >= 28 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 9
    @JvmStatic
    val isAtLeastPie: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P || Build.VERSION.SDK_INT >= 27 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 8
    @JvmStatic
    val isAtLeastOreo: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O || Build.VERSION.SDK_INT >= 25 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 7
    @JvmStatic
    val isAtLeastN: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N || Build.VERSION.SDK_INT >= 23 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 7.1
    @JvmStatic
    val isAtLeastN_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 || Build.VERSION.SDK_INT >= 24 && Build.VERSION.PREVIEW_SDK_INT == 1

    // 6
    @JvmStatic
    val isAtLeastM: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    // 5
    val isAtLeastL_MR1: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1

    // 5
    @JvmStatic
    val isL: Boolean get() = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP

}