package bryan.miranda.crud

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Splash_Screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val tiempo = 3000
        val handler = android.os.Handler()

        handler.postDelayed({
            val pantalla = Intent(this, MainActivity::class.java)
            startActivity(pantalla)
        }, tiempo.toLong())
    }
}