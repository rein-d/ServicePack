package corp.remotehelp.integration.evotor.ru.utils

import android.view.View
import androidx.databinding.BindingAdapter

object ViewConfiguration {

    @BindingAdapter("bind:visibleGone")
    @JvmStatic
    fun showHideGone(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @BindingAdapter("bind:visibleInvisible")
    @JvmStatic
    fun showHideInvisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }
}