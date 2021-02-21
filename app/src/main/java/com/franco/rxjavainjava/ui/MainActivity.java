package com.franco.rxjavainjava.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.franco.rxjavainjava.R;
import com.franco.rxjavainjava.domain.Model.Comment;
import com.franco.rxjavainjava.domain.Model.Post;
import com.franco.rxjavainjava.domain.Model.Task;
import com.franco.rxjavainjava.network.ServiceGenerator;
import com.franco.rxjavainjava.util.DataSource;

import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.annotations.NonNull;
//import io.reactivex.rxjava3.core.BackpressureStrategy;
//import io.reactivex.rxjava3.core.Flowable;
//import io.reactivex.rxjava3.core.FlowableSubscriber;
//import io.reactivex.rxjava3.core.Observable;
//import io.reactivex.rxjava3.core.ObservableEmitter;
//import io.reactivex.rxjava3.core.ObservableOnSubscribe;
//import io.reactivex.rxjava3.core.ObservableSource;
//import io.reactivex.rxjava3.core.Observer;
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//import io.reactivex.rxjava3.disposables.Disposable;
//import io.reactivex.rxjava3.functions.Function;
//import io.reactivex.rxjava3.functions.Predicate;
//import io.reactivex.rxjava3.schedulers.Schedulers;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    public static final String TAG="MainActivity";

    private final CompositeDisposable compositeDisposable1= new CompositeDisposable();
    private final CompositeDisposable compositeDisposable2= new CompositeDisposable();
    private RecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;



    /**
     * In RxJava2 there are two observable object classes
     * Observable- backPressure-Unaware
     * Flowable   -backPressure-Aware
     *
     * BackPressure:(def) Backpressure is when in a Flowable processing pipeline can't process the values
     * fast enough and need a way to tell the upstream producer to slow down.
     *
     * This means if you have a list emitting to much values you could run into OUT OF MEMORY EXCEPTION on Observables.
     * all observables are emitted lazily.
     *
     * HotResources: is when the observer cannot handle what they are observing and this. some objects are received or emitted if the
     * observer cannot keep up this objects need to be buffered.
     *
     * Cold Resources:
     *
     * This is the opposite of hot sources. The objects emitted by the Observables are said to be emitted lazily. Meaning: The Observables begin emitting objects when the Observers want and at a rate suitable to the Observer.
     *
     * The objects emitted by the Observables do not need to be buffered because the whole process is basically at the discretion of the Observer.
     *
     * Think of this as a pull relationship. The Observers pull objects from the Observables when they want.
     *
     * source:Mitch Tabian
     *
     *
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView=findViewById(R.id.recycler_view);


        Observable<Task> taskObservable = Observable // create a new Observable object
                .fromIterable(DataSource.createTasksList()) // apply 'fromIterable' operator
                .subscribeOn(Schedulers.io())// designate worker thread (background)
                .filter(new Predicate<Task>() {
                    @Override
                    public boolean test(Task task) {
                        Log.d(TAG,"onNext: "+Thread.currentThread().getName());
                        try {
                            Thread.sleep(1000);// Since this iterates 5 times and you run it on taskObservable it will block the UI but here is in IO

                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        return task.isComplete();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread()); // designate observer thread (main thread)

        taskObservable.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG,"onSubcribe: called.");
                compositeDisposable1.add(d);
            }
            @Override
            public void onNext(Task task) { // run on main thread
                Log.d(TAG,"onNext: "+Thread.currentThread().getName());
                Log.d(TAG, "onNext: : " + task.getDescription());

            }
            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error : " +e);

            }
            @Override
            public void onComplete() {
                Log.d(TAG,"onComplete:  called");
            }
        });

        /**
         * This code wont cause out of memory exception
         */
        Flowable.range(1,100000)
                .onBackpressureBuffer()
                .observeOn(Schedulers.computation())
                .subscribe(new FlowableSubscriber<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Subscription s) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         * Observable to Flowable... Flowable to Observable
         */

        /**
         * Observable ->Flowable
         * You need to define the buffer strategy
         * more info on each http://reactivex.io/RxJava/2.x/javadoc/io/reactivex/BackpressureStrategy.html
         */

        Observable<Integer> observable = Observable
                .just(1, 2, 3, 4, 5);

        Flowable<Integer> flowable = observable.toFlowable(BackpressureStrategy.BUFFER);

        /**
         * Operators
         * create() Input: T output Observable <T>
         *     range(1, 10).repeat()
         */

        Observable<Integer> obInts1= Observable.range(1,10).repeat(5);

        final Task task =new Task("Take out the garbage",false,10);

        Observable<Task> garbageTask = Observable
                .create(new ObservableOnSubscribe<Task>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Task> emitter) {

                        /**
                         * For a single object
                         */
                        if(!emitter.isDisposed()){
                            emitter.onNext(task);// since it is not a list the we call onComplete() if it was a list call onNext()
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * For a List
         */

//        final List<Task> list = DataSource.createTasksList();
//        for(Task task1:list){
//            if(!emmiter.isDisposed()){
//                emmiter.onNext(task);
//            }
//
//        }
//
//        if(!emmiter.isDisposed()){
//            emmiter.onComplete();
//        }


        garbageTask.subscribe(new Observer<Task>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Task task) {

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });


        /**
         * Just() just can only pass 10 object MAX
         */
        Observable.just(1,2,3,4,5,6,7,8,9,10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.d(TAG,"onNext"+integer);
                        /**
                         * output: 1,2,3,4 ....10
                         */

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         * Range()
         */

        Observable.range(1, 50)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         * Repeat()
         */

        Observable.just(0,3)
                .repeat(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        /**
                         * output = 0 1 2 0 1 2
                         */
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

        /**
         * Map()
         */
         Observable<Task> obser = Observable
                 .range(1,10)
                 .subscribeOn(Schedulers.io())
                 .map(new Function<Integer, Task>() {
                     @Override
                     public Task apply(Integer integer)  {
                         return null;
                     }
                 });

        Observable<Long> intervalObservable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .takeWhile(new Predicate<Long>() { // stop the process if more than 5 seconds passes
                    @Override
                    public boolean test(Long aLong) throws Exception {
                        return aLong <= 5;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
        intervalObservable.subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull Long aLong) {
                Log.d(TAG, "onNext: interval: " + aLong);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

        /**
         * timer()
         * Observable<Long> timeObservable = Observable
         *         .timer(3, TimeUnit.SECONDS)
         *         .subscribeOn(Schedulers.io())
         *         .observeOn(AndroidSchedulers.mainThread());
         */


        Task[] list = new Task[5];
        list[0] = (new Task("Take out the trash", true, 3));
        list[1] = (new Task("Walk the dog", false, 2));
        list[2] = (new Task("Make my bed", true, 1));
        list[3] = (new Task("Unload the dishwasher", false, 0));
        list[4] = (new Task("Make dinner", true, 5));

        Observable<Task> taskObservable2 = Observable
                .fromArray(list)// <-------------
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        /**
         * PERFECT FOR DATABASE AND RETROFIT CALLS
         * Observable<Task> callable = Observable
         *         .fromCallable(new Callable<Task>() {
         *             @Override
         *             public Task call() throws Exception {
         *                 return MyDatabase.getTask();
         *             }
         *         })
         *         .subscribeOn(Schedulers.io())
         *         .observeOn(AndroidSchedulers.mainThread());
         *
         *         YOU CAN SUBSTITUDE ALSO LIST<TASK>
         */


        //FromFuture() OPERATORS
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        try {
            viewModel.makeFutureQuery().get()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d(TAG, "onSubscribe: called.");
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            Log.d(TAG, "onNext: got the response from server!");
                            try {
                                Log.d(TAG, "onNext: " + responseBody.string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: ", e);
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete: called.");
                        }
                    });
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        /**
         * OPERATORS
         * fromPublisher()
         */
        // fromPublisher turn from Flowable to LiveData and inverse
        viewModel.makeQuery().observe(this, new androidx.lifecycle.Observer<ResponseBody>() {
            @Override
            public void onChanged(ResponseBody responseBody) {
                Log.d(TAG, "onChanged: this is a live data response!");
                try {
                    Log.d(TAG, "onChanged: " + responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        /**
         * Flatmap() OPERATOR EX
         */
        initRecyclerView(recyclerView);

        getPostsObservable()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<Post, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(Post post) throws Exception {
                        return getCommentsObservable(post);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable2.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        updatePost(post);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Observable<Post> getPostsObservable(){
        return ServiceGenerator.getRequestApi()
                .getPosts()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Post>, ObservableSource<Post>>() {
                    @Override
                    public ObservableSource<Post> apply(final List<Post> posts) throws Exception {
                        adapter.setPosts(posts);
                        return Observable.fromIterable(posts)
                                .subscribeOn(Schedulers.io());
                    }
                });
    }

    private void updatePost(final Post p){
        Observable
                .fromIterable(adapter.getPosts())
                .filter(new Predicate<Post>() {
                    @Override
                    public boolean test(Post post) throws Exception {
                        return post.getId() == p.getId();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Post>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable2.add(d);
                    }

                    @Override
                    public void onNext(Post post) {
                        Log.d(TAG, "onNext: updating post: " + post.getId() + ", thread: " + Thread.currentThread().getName());
                        adapter.updatePost(post);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private Observable<Post> getCommentsObservable(final Post post){
        return ServiceGenerator.getRequestApi()
                .getComments(post.getId())
                .map(new Function<List<Comment>, Post>() {
                    @Override
                    public Post apply(List<Comment> comments) throws Exception {

                        int delay = ((new Random()).nextInt(5) + 1) * 1000; // sleep thread for x ms
                        Thread.sleep(delay);
                        Log.d(TAG, "apply: sleeping thread " + Thread.currentThread().getName() + " for " + String.valueOf(delay)+ "ms");

                        post.setComments(comments);
                        return post;
                    }
                })
                .subscribeOn(Schedulers.io());

    }

    private void initRecyclerView(RecyclerView recyclerView){
        adapter = new RecyclerAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable1.clear();
        /**
         * If you call dispose you disable the Observable to have more observers.
         */

        /**
         * If you are using mvvm mvp use onClear()
         */
    }
}

