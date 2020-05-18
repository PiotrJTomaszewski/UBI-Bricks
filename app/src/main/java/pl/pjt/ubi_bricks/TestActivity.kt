//package pl.pjt.ubi_bricks
//
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import com.google.android.material.snackbar.Snackbar
//import androidx.appcompat.app.AppCompatActivity
//import androidx.lifecycle.lifecycleScope
//
//import kotlinx.android.synthetic.main.activity_test.*
//import java.net.URL
//import kotlinx.coroutines.*
//
//class TestActivity : AppCompatActivity() {
//
//    var image: Bitmap? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_test)
//        setSupportActionBar(toolbar)
//
//        lifecycleScope.launch {
//            // This will run in a background of the whole application
//            var image = getBitmapFromURL("https://www.lego.com/service/bricks/5/2/300126")
//            if (image != null) {
//                runOnUiThread {
//                    setsImage(image)
//                }
//            }
//        }
//
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//    }
//
//
//    suspend fun getBitmapFromURL(url: String): Bitmap? {
//        var image: Bitmap? = null
//        withContext(Dispatchers.IO) {
//            val connection = URL(url).openConnection()
//            connection.doInput = true
//            connection.connect()
//            val inputStream = connection.getInputStream()
//            image = BitmapFactory.decodeStream(inputStream)
//        }
//        return image
//    }
//
//    fun setsImage(image: Bitmap) {
//        imageView.setImageBitmap(image)
//    }
//
//
//}
