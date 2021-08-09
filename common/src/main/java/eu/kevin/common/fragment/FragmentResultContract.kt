package eu.kevin.common.fragment

import android.os.Bundle

abstract class FragmentResultContract<T> {
    abstract val requestKey: String
    abstract val resultKey: String
    abstract fun parseResult(data: Bundle): T
}