package com.virtual.box.base.util.compat

import android.os.Build
import android.os.SystemProperties
import com.virtual.box.base.ext.isNotNullOrEmpty
import java.util.*

object RomCompat {

    private const val KEY_VERSION_MIUI = "ro.miui.ui.version.name"
    private const val KEY_VERSION_EMUI = "ro.build.version.emui"
    private const val KEY_VERSION_OPPO = "ro.build.version.opporom"
    private const val KEY_VERSION_VIVO = "ro.vivo.os.version"

    val isMIUI: Boolean
        get() = SystemProperties.get(KEY_VERSION_MIUI).isNotNullOrEmpty()

    val isEMUI: Boolean
        get() = SystemProperties.get(KEY_VERSION_EMUI).isNotNullOrEmpty()

    val isOppo:Boolean
        get() = SystemProperties.get(KEY_VERSION_OPPO).isNotNullOrEmpty()

    val isVivo: Boolean
        get() = SystemProperties.get(KEY_VERSION_VIVO).isNotNullOrEmpty()

    val isSamsung: Boolean
        get() = "samsung".equals(Build.BRAND, ignoreCase = true)
                || "samsung".equals(Build.MANUFACTURER, ignoreCase = true)

    val isFlyme: Boolean
        get() = Build.DISPLAY.uppercase(Locale.CANADA).contains("flyme")

    private var sRomType: ROMType? = null

    val rOMType: ROMType?
        get() {
            if (sRomType == null) {
                sRomType = when {
                    isEMUI -> {
                        ROMType.EMUI
                    }
                    isMIUI -> {
                        ROMType.MIUI
                    }
                    isFlyme -> {
                        ROMType.FLYME
                    }
                    isVivo -> {
                        ROMType.VIVO
                    }
                    isSamsung -> {
                        ROMType.SAMSUNG
                    }
                    else -> {
                        ROMType.OTHER
                    }
                }
            }
            return sRomType
        }

    enum class ROMType {
        EMUI, MIUI, FLYME, VIVO, SAMSUNG, OTHER
    }
}