package ru.jollydroid.rxbinding1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.Toast



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        button.isEnabled = false
//        button.isClickable = false

        val isEmailValid = email
            .textChanges()
            .map { t -> EMAIL_ADDRESS.matcher(t).matches() }
            .distinctUntilChanged()

        val isPasswordValid = password
            .textChanges()
            .map { t -> (t.length > 4) }
            .distinctUntilChanged()

        isPasswordValid
            .doOnNext { Log.d("happy", "password $it") }
            .map { b -> if (b) Color.BLACK else Color.RED }
            .subscribe { c ->
                password.setTextColor(c)
            }

        isEmailValid
            .doOnNext { Log.d("happy", "email $it") }
            .map { b -> if (b) Color.BLACK else Color.RED }
            .subscribe { c -> email.setTextColor(c) }

        // note                vvvvvvvvvvv: Observables (from RxKotlin), not Observable
        val isSignUpPossible =
            Observables
                .combineLatest(isEmailValid, isPasswordValid) { e, p -> e && p }
                .distinctUntilChanged()

        isSignUpPossible
            .subscribe { b ->
                Log.d("happy", "button $b")
                button.isEnabled = b
                button.isClickable = b
            }

        button
            .clicks()
            .doOnNext { Log.d("happy", "click") }
            .subscribe { signUpButtonAction() }
    }

    private fun signUpButtonAction() {
        Toast.makeText(this@MainActivity, "Button clicked", Toast.LENGTH_LONG).show()
    }

}