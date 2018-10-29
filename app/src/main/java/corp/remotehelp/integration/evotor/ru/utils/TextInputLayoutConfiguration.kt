package corp.remotehelp.integration.evotor.ru.utils

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

object TextInputLayoutConfiguration {
    @BindingAdapter("bind:error")
    @JvmStatic
    fun bindError(view: TextInputLayout, errorMessage: String?) {
        if (errorMessage != null) {
            view.error = errorMessage
        }
    }
}