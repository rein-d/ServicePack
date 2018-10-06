package corp.remotehelp.integration.evotor.ru.samporemotehelp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity2_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, StartFragment())
                    .commitNow()
        }
    }

    fun success() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SuccessFragment())
                .commitNow()
    }
}
