package coder.zhang.rxjavaproject.retrofit_rxjava;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import coder.zhang.rxjavaproject.R;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RetrofitRxJavaActivity extends AppCompatActivity {

    private Button registBtn, loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrofit);

        registBtn = findViewById(R.id.regist);
        loginBtn = findViewById(R.id.login);
    }

    public void regist(View view) {
        MyRetrofit.createRetrofit().create(INetworkInterface.class)
                .regist("张三丰", "123123")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RegistResponse>() {
                    @Override
                    public void accept(RegistResponse registResponse) throws Exception {

                    }
                });
    }

    public void login(View view) {
        MyRetrofit.createRetrofit().create(INetworkInterface.class)
                .login("张三丰", "123123")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginResponse>() {
                    @Override
                    public void accept(LoginResponse loginResponse) throws Exception {

                    }
                });
    }

    public void registAndLogin(View view) {
        // 1、调用注册接口
        // 2、注册成功，更新注册UI
        // 3、自动调用登录接口
        // 4、登录成功，更新登录UI

        MyRetrofit.createRetrofit().create(INetworkInterface.class)
                .regist("张三丰", "123123")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<RegistResponse>() {
                    @Override
                    public void accept(RegistResponse registResponse) throws Exception {
                        // 更新注册UI
                        registBtn.setText("注册成功");
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<RegistResponse, ObservableSource<LoginResponse>>() {
                    @Override
                    public ObservableSource<LoginResponse> apply(RegistResponse registResponse) throws Exception {
                        return MyRetrofit.createRetrofit().create(INetworkInterface.class).login("张三丰", "123123");
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        showProgressDialog();
                    }

                    @Override
                    public void onNext(LoginResponse loginResponse) {
                        // 更新登录UI
                        loginBtn.setText("登录成功");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        hideProgressDialog();
                    }
                });
    }

    private ProgressDialog progressDialog;
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在请求...");
        }
        progressDialog.show();
    }
    private void hideProgressDialog() {
        if (progressDialog != null) progressDialog.dismiss();
    }
}
