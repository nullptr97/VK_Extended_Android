package com.extended.vk.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.extended.vk.R
import com.extended.vk.api.Api
import com.extended.vk.api.Auth
import com.extended.vk.helpers.Constants
import com.extended.vk.utils.Utils
import com.extended.vk.utils.WrongResponseCodeException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream

class LoginActivity: AppCompatActivity() {
    private lateinit var loginEditText: KeyboardEditText
    private lateinit var passwordEditText: KeyboardEditText
    private lateinit var authButton: Button
    private lateinit var authProgress: ProgressBar

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginEditText = findViewById(R.id.login_text)
        passwordEditText = findViewById(R.id.password_text)
        authButton = findViewById(R.id.auth_button)
        authProgress = findViewById(R.id.auth_progress)

        authProgress.visibility = View.GONE

        loginEditText = findViewById(R.id.login_text)
        passwordEditText = findViewById(R.id.password_text)

        loginEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            onKeyboardTracking(hasFocus)
        }
        passwordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            onKeyboardTracking(hasFocus)
        }

        loginEditText.listener = object : KeyboardEditText.Listener {
            override fun onImeBack(editText: KeyboardEditText) {
                editText.clearFocus()
            }
        }

        passwordEditText.listener = object : KeyboardEditText.Listener {
            override fun onImeBack(editText: KeyboardEditText) {
                editText.clearFocus()
            }
        }

        authButton.setOnClickListener {
            hideKeyboard(this)
            loginEditText.clearFocus()
            passwordEditText.clearFocus()
            authProgress.visibility = View.VISIBLE
            authButton.alpha = 0.5f
            if (loginEditText.text.toString().isNotEmpty() && passwordEditText.text.toString().isNotEmpty()) {
                object: Thread() {
                    override fun run() = try {
                        val url = Auth.getDirectAuthUrl(Constants.clientAppId, Constants.clientSecretKey, loginEditText.text.toString(), passwordEditText.text.toString(), Auth.getSettings())
                        val jsonObjectString = sendRequestInternal(url).toString()
                        val loginData = JSONObject(jsonObjectString)
                        runOnUiThread(successRunnable(loginData))
                    } catch (e:Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Toast.makeText(applicationContext, "Заполните все поля", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun successRunnable(jsonObject: JSONObject): Runnable = Runnable {
        parseSuccessJSON(jsonObject.optString("access_token"), jsonObject.optString("user_id"))
    }

    private fun parseSuccessJSON(accessToken: String, userId: String) {
        val intent = Intent()
        intent.putExtra("token", accessToken)
        intent.putExtra("user_id", java.lang.Long.parseLong(userId))
        intent.putExtra("is_login", true)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d("Configuration", "Configuration changed: $newConfig")
    }

    override fun onBackPressed() {
        Toast.makeText(this, "Вы не авторизовались", Toast.LENGTH_LONG).show()
    }

    private fun onKeyboardTracking(isShow: Boolean) {
        val relativeLayout = findViewById<RelativeLayout>(R.id.login_container)
        val rootWindow = window
        val view = rootWindow.decorView
        val r = Rect()
        relativeLayout.viewTreeObserver.addOnGlobalLayoutListener {
            view.getWindowVisibleDisplayFrame(r)
            Log.d("Keyboard tracking", "$r")
            val linearParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT))
            linearParams.setMargins(0, 0, 0, if (isShow) 618 else 0)
            relativeLayout.layoutParams = linearParams
            relativeLayout.requestLayout()
        }
    }

    @Throws(IOException::class)
    private fun sendRequestInternal(url: String): String? {
        var connection: HttpURLConnection? = null
        return try {
            connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.useCaches = false
            connection.doOutput = false
            connection.doInput = true
            connection.requestMethod = "GET"
            if (Api.enable_compression) connection.setRequestProperty("Accept-Encoding", "gzip")
            //if (is_post) connection.outputStream.write(body.toByteArray(charset("UTF-8")))
            val code = connection.responseCode
            Log.i("Api.TAG", "code=$code")
            if (code == -1) throw WrongResponseCodeException("Network error")
            //может стоит проверить на код 200
            //on error can also read error stream from connection.
            var `is`: InputStream =
                BufferedInputStream(connection.inputStream, 8192)
            val enc = connection.getHeaderField("Content-Encoding")
            if (enc != null && enc.equals("gzip", ignoreCase = true)) `is` =
                GZIPInputStream(`is`)
            Utils.convertStreamToString(`is`)
        } finally {
            connection?.disconnect()
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
class KeyboardEditText: AppCompatEditText {
    var listener: Listener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            listener?.onImeBack(this)
        }
        return super.dispatchKeyEvent(event)
    }

    interface Listener {
        fun onImeBack(editText: KeyboardEditText)
    }

}