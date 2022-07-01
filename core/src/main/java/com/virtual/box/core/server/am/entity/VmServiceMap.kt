package com.virtual.box.core.server.am.entity

import android.content.Intent
import androidx.collection.ArrayMap

/**
 *
 * @author  zhangzhipeng
 * @date    2022/6/29
 */
class VmServiceMap(
    val userId: Int
) {

    val runningService = ArrayMap<Intent.FilterComparison, VmServiceRecord>()

}