package corp.remotehelp.integration.evotor.ru

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class StartViewModel : ViewModel() {

    val client = OkHttpClient.Builder()
            .hostnameVerifier { hostname, session -> true }
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
                Log.d("Http", it)
            })
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
            .dispatcher(Dispatcher())
            .build()

    val gson = GsonBuilder()
            .create()

    lateinit var activity: MainActivity

    val name = MutableLiveData<String>()
    val nameError = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val phoneError = MutableLiveData<String>()

    fun init(activity: MainActivity) {
        this.activity = activity
    }

    fun help() {
        nameError.value = null
        phoneError.value = null
        if (name.value.isNullOrEmpty()) {
            nameError.value = "Имя не может быть пустым"
            return
        }
        if (phone.value.isNullOrEmpty()) {
            phoneError.value = "Телефон не может быть пустым"
            return
        }
        GlobalScope.launch {
            val response = GlobalScope.async {
                try {
                    ctoHelp()
                } catch (ex: IOException) {
                    null
                }
            }.await()

            val ctoHelpResponse = response.use {
                if (it?.isSuccessful == true) {
                    it.body()?.let { responseBody ->
                        gson.fromJson<CtoHelpResponse>(responseBody.charStream(), CtoHelpResponse::class.java)
                    }
                } else {
                    null
                }
            }

            launch(Dispatchers.Main) {
                ctoHelpResponse?.let {
                    if (it.result == 0L) {
                        activity.success()
                    } else {
                        Toast.makeText(activity, it.error_message, Toast.LENGTH_LONG).show()
                    }
                } ?: run {
                    Toast.makeText(activity, "Не удалось отправить запрос. Неизвестная ошибка", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun ctoHelp(): Response {
        val body = RequestBody.create(JSON, "{ \"email\": \"${activity.getString(R.string.email)}\", \"info\": \"${name.value}, ${phone.value}\"}")
        val request = Request.Builder()
                .url(URL + "cto-help")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

        return client.newCall(request).execute()

    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        const val URL = "https://remote2.evotor.ru/remote/"
    }

}