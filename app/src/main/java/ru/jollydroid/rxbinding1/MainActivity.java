package ru.jollydroid.rxbinding1;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import ru.jollydroid.rxbinding1.databinding.ActivityMainBinding;
import rx.Observable;
import rx.Subscriber;

import static android.support.v4.util.PatternsCompat.EMAIL_ADDRESS;
import static com.jakewharton.rxbinding.view.RxView.clicks;
import static com.jakewharton.rxbinding.widget.RxTextView.textChanges;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Observable<Boolean> emailValid;
    private Observable<Boolean> passwordValid;
    private Observable<Boolean> signUpPossible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        emailValid =
                Observable
                        .create(new Observable.OnSubscribe<CharSequence>() {
                            @Override
                            public void call(Subscriber<? super CharSequence> subscriber) {

                                binding.email.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        subscriber.onNext(s);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                            }
                        })
                        .map(t -> EMAIL_ADDRESS.matcher(t).matches());

        /*

        Тоже самое ^^^^^^^^ можно записать короче, см. закомментированный код про emailValid ниже,
        а также живой код про passwordValid еще тремя строчками ниже.

        emailValid = textChanges(binding.email)
                .map(t -> EMAIL_ADDRESS.matcher(t).matches());
        */

        passwordValid = textChanges(binding.password)
                .map(t -> t.length() > 4);

        emailValid
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(c -> binding.email.setTextColor(c));

        passwordValid
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(c -> binding.password.setTextColor(c));

        signUpPossible = Observable.combineLatest(emailValid, passwordValid, (e, p) -> e && p);

        signUpPossible
                .subscribe(b -> binding.button.setEnabled(b));

        clicks(binding.button)
                .subscribe(o -> signUpButtonAction());

        emailValid
                .map(b -> b ? Color.BLACK : Color.RED)
                .subscribe(c -> binding.email.setTextColor(c));
    }

    private void signUpButtonAction() {
        Toast.makeText(MainActivity.this, "Button clicked", Toast.LENGTH_LONG).show();
    }

}
