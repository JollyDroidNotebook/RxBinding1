package ru.jollydroid.rxbinding1

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.PatternsCompat.EMAIL_ADDRESS
import androidx.lifecycle.Lifecycle
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChanges
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle
import com.trello.rxlifecycle3.kotlin.bindToLifecycle
import com.trello.rxlifecycle3.kotlin.bindUntilEvent
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        val lifecycleProvider = AndroidLifecycle.createLifecycleProvider(this)


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
            .bindToLifecycle(lifecycleProvider)
            .subscribe { c -> password.setTextColor(c) }

        isEmailValid
            .map { b -> if (b) Color.BLACK else Color.RED }
            .bindToLifecycle(lifecycleProvider)
            .subscribe { c -> email.setTextColor(c) }

        // note                vvvvvvvvvvv: Observables (from RxKotlin), not Observable
        val isSignUpPossible = Observables
            .combineLatest(isEmailValid, isPasswordValid) { e, p -> e && p }
            .distinctUntilChanged()

        isSignUpPossible
            .bindToLifecycle(lifecycleProvider)
            .subscribe { b ->
                button.isEnabled = b
                button.isClickable = b
            }

        button.clicks()
            .bindToLifecycle(lifecycleProvider)
            .subscribe { signUpButtonAction() }

        val ticker = Observable
            .interval(1, TimeUnit.SECONDS, Schedulers.io())
            .share()

        ticker
            .bindToLifecycle(lifecycleProvider)
            .subscribe { o -> Log.d("happy", "tick " + o) }

        lifecycleProvider.lifecycle()
            .bindToLifecycle(lifecycleProvider)
            .filter { it == Lifecycle.Event.ON_RESUME }
            .subscribe {
                ticker
                    .bindUntilEvent(lifecycleProvider, Lifecycle.Event.ON_PAUSE)
                    .subscribe { o -> Log.d("happy", "tack " + o) }
            }

    }

    private fun signUpButtonAction() =
        Toast.makeText(this@MainActivity, "Button clicked", Toast.LENGTH_LONG).show()
}
