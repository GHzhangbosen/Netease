package coder.zhang.rxjavaproject.retrofit_rxjava;

import io.reactivex.Observable;

public interface INetworkInterface {

    public Observable<RegistResponse> regist(String username, String password);

    public Observable<LoginResponse> login(String username, String password);
}
