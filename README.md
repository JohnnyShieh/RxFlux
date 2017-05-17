# Flux Architecture on Android

I have tried MVC, MVP and Clean architecture on Android. But I love the Flux architecture mostly because of its **undirectional data flow** and **layering** features. However I can't find an approving implementation of Flux arichtecture, so I wirte RxFlux based on skimarxall's [RxFlux](https://github.com/skimarxall/RxFlux).

## Getting started

**Gradle**

```
dependencies {
  compile 'com.johnny.rxflux:rxflux:1.0.2'
}
```

## Introducing Flux Architecture

**[Flux Architecture][flux-arch]** is used by Facebook to build their client- side web applications. Like _Clean Architecture_ it is not intended for mobile apps, but its features and simplicity will allow us to adapt it very well to Android projects.

![](https://github.com/JohnnyShieh/RxFlux/blob/master/images/flux-graph-simple.png)

There are two **key features** to understand Flux:

* The data flow is always **unidirectional**.

    An [unidirectional data flow](https://www.youtube.com/watch?v=i__969noyAM) is the **core** of the Flux architecture and is what makes it so easy to learn.
It also provides great advantages when testing the application as discussed below.

* The application is divided into **three main parts**:

    - **View**: Application interface. It create actions in response to user interactions.
    - **Dispatcher**: Central hub through which pass all actions and whose responsibility is to make them arrive to every Store.
    - **Store**: Maintain the state for a particular application domain. They respond to actions according to current state, execute business logic and emit a _change_ event when they are done. This event is used by the view to update its interface.

This three parts communicate through **Actions**: Simple plain objects, identified by a type, containing the data related to that action.

## Flux Android Architecture

First step is to **map Flux elements with Android app components**.

Two of this elements are very easy to figure out and implement.

- **View**: Activity or Fragment
- **Dispatcher**: An event bus. I use RxBus in my implementation.

### Actions

Actinos are not complex either. They will be implemented as simple POJOs with two main attributes:

- Type: a `String` identifying the type of action.
- Data: a `ArrayMap` with the payload for action.

For example, a action to create todo will like this:

```java
Action action = Action.type("todo_create")
    .bundle("key_text"", text)
    .build();
```

There has another class `ErrorAction` which I think need to pay attention. For example, when we make a asynchronous network call Action, if there happens some exception we need send the Action and error info to Store. And it's what ErrorAction contains.

### Store

This is perhaps the **most difficult** to get Flux concept.

Stores contain the **status of the application and its business logic**. They are similar to _rich data models_ but they can manage the status of **various objects**, not just one.

Stores **react to Actions emitted by the Dispatcher**, execute business logic and emit a change event as result.

Stores only output is this single event: _change_. View(flux component) interested in a Store internal status must listen to this event and use it to get the data it needs.

No other component of the system should need to know anything about the status of the application.

Finally, stores must **expose an interface** to obtain application Status. This way, view elements can query the Stores and update application UI in response.

![](https://github.com/JohnnyShieh/RxFlux/blob/master/images/flux-store.png)

In my implementation, a Store object can only be listened to one View, but one View can listen to multiple Store object. The reason is Store maintains the part state of a View, so if another View interested the same Store, it can listen to another instance of the same Store.

### Network requests and asynchronous calls

In the initial Flux graph I intentionally skipped one part: **network calls**. Next graph completes first one adding more details:

![](https://github.com/JohnnyShieh/RxFlux/blob/master/images/flux-graph-complete.png)

Asynchronous network calls are triggered from an **Actions Creator**.
A Network Adapter makes the asynchronous call to the corresponding API and returns the result to the Actions Creator.

Finally the Actions Creator dispatch the corresponding typed Action with returned data.

Having all the network and asynchronous work out of the Stores has has **two main advantages**:

- **Your Stores are completely synchronous**: This makes the logic inside a Store very easy to follow. Bugs will be much easier to trace. And since **all state changes will be synchronous** testing a Store becomes an easy job: launch actions and assert expected final state.

- **All actions are triggered from an Action Creator**: Having a single point at which you create and launch all user actions greatly simplifies finding errors.
Forget about digging into classes to find out where an action is originated. **Everything starts here**. And because asynchronous calls occur _before_, everything that comes out of ActionCreator is synchronous. This is a huge win that significantly improves traceability and testability of the code.

# The Implementation of Android Flux Architecture

I use RxBus based on RxJava 2.x as the event bus, so the name is RxFlux.

Imagine a scenario where you pull tasks from network and show them, if you pull failed show the error msg. Then how to implement in RxFlux:

Firstly we need a network call(usually Retrofit).

```java
// Api

@GET(Api.GET_TASKS)
Single<List<Task>> getTasks();
```

Next we should make an action:

```java
// TaskActionCreator

public void getTasks() {
    final Action action = Action.type("get_task").build();
    api.getTasks()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<List<Task>>() {
            @Override
            public void accept(@NonNull List<Task> tasks) throws Exception {
                action.getData().put("key_tasks", tasks);
                Dispatcher.get().postAction(action);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                hasAction = false;
                Dispatcher.get().postError(action, throwable);
            }
        });
}
```

Then we handle store:

```java
// TasksStore

public class TasksStore extends Store<Store.StoreChangeEvent> {
    private List<Task> mTasks;
    private String mErrorMsg;

    @Override
    public void onAction(Action action) {
        mErrorMsg = null;
        mTasks = action.get("key_tasks");
        postChange(new Store.StoreChangeEvent());
    }

    @Override
    public void onError(Action action, Throwable throwable) {
        mErrorMsg = "request failed!"; // exactly we should set msg due to throwable
        postError(new Store.StoreChangeEvent());
    }

    public List<Task> getTasks() {
        return mTasks;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }
}
```

Finally we should handle activity:

```java
// TaskActivity implements StoreObserver<Store.StoreChangeEvent>

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mStore = new TaskStore();
    mStore.setObserver(this);
    Dispatcher.get().register(mStore, "get_tasks"); // store register get_tasks action
    ...
}

@Override
protected void onDestroy() {
    super.onDestroy();
    mTodoStore.unRegister();
}

@Override
public void onChange(Store.StoreChangeEvent event) {
    // update ui
}

@Override
public void onError(Store.StoreChangeEvent event) {
    // show error msg
}
```

More detail samples are here:

* [todo-smaple](https://github.com/JohnnyShieh/RxFlux/tree/master/todo)

* [gank](https://github.com/JohnnyShieh/Gank)


## Conclusion

There is **no such thing as the Best Architecture for an Android app**.
There _is_ the Best Architecture for your current app. And it is the one that let you collaborate with your teammates easily, finish the project on time, with quality and as less bugs as possible.

I believe Flux is very good for all of that.



[flux-arch]:https://facebook.github.io/flux/docs/overview.html