package ru.jollydroid.rxbinding1

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.rxkotlin.Observables
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val isEmailValid = email.textChanges()
            .map { t -> EMAIL_ADDRESS.matcher(t).matches() }
            .distinctUntilChanged()

        val isPasswordValid = password.textChanges()
            .map { t -> (t.length > 4) }
            .distinctUntilChanged()

        isPasswordValid
            .map { b -> if (b) Color.BLACK else Color.RED }
            .subscribe { c -> password.setTextColor(c) }

        isEmailValid
            .map { b -> if (b) Color.BLACK else Color.RED }
            .subscribe { c -> email.setTextColor(c) }

        // note                vvvvvvvvvvv: Observables (from RxKotlin), not Observable
        val isSignUpPossible = Observables
                .combineLatest(isEmailValid, isPasswordValid) { e, p -> e && p }
                .distinctUntilChanged()

        isSignUpPossible
            .subscribe { b ->
                button.isEnabled = b
                button.isClickable = b
            }

        button.clicks()
            .subscribe { signUpButtonAction() }
    }

    private fun signUpButtonAction() =
        Toast.makeText(this@MainActivity, "Button clicked", Toast.LENGTH_LONG).show()
}
