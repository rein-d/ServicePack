package corp.remotehelp.integration.evotor.ru.samporemotehelp


import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_start.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException


class StartFragment : Fragment() {

    val client = OkHttpClient.Builder()
            .hostnameVerifier { hostname, session -> true }
            .addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { Log.e("Http", it) })
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
            .dispatcher(Dispatcher())
            .build()

    val gson = GsonBuilder()
            .create()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        help.setOnClickListener { _ ->
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
                            (activity as MainActivity).success()
                        } else {
                            Toast.makeText(context, it.error_message, Toast.LENGTH_LONG).show()
                        }
                    } ?: run {
                        Toast.makeText(context, "Не удалось отправить запрос. Неизвестная ошибка", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun ctoHelp(): Response {
        val body = RequestBody.create(JSON, EMAIL)
        val request = Request.Builder()
                .url(URL + "cto-help")
                .addHeader("Authorization", "Basic YXBwXzkxOWIzMWQ0LTg0ZTItNDM2Zi1hNzgwLTU3YjYxZTJlMWFjZjpRdWFpZ2U5aFJpZTRPaGo1ZWlUaGVlYzI=")
                .addHeader("Content-Type", "application/json")
                .addHeader("x-device-id", "952398083823999")
                .post(body)
                .build()

        return client.newCall(request).execute()

    }

    companion object {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        const val URL = "https://remote2.evotor.ru/remote/"
        const val EMAIL = "{ \"email\": \"spelliar@gmail.com\" }"
    }

}

data class CtoHelpResponse(val result: Long, val error_message: String)
