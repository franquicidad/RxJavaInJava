package com.franco.rxjavainjava.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.franco.rxjavainjava.domain.Repository;

import java.util.concurrent.Future;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.ResponseBody;

public class MainViewModel extends ViewModel {

    private Repository repository;

    public MainViewModel() {
        repository = Repository.getInstance();
    }

    public Future<Observable<ResponseBody>> makeFutureQuery(){
        return repository.makeFutureQuery();
    }

    public LiveData<ResponseBody> makeQuery(){
        return repository.makeReactiveQuery();
    }
}
