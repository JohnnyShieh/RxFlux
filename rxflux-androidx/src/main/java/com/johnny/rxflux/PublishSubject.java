/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.johnny.rxflux;

import android.util.Log;

import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.Nullable;
import io.reactivex.annotations.NonNull;
import java.util.concurrent.atomic.*;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.functions.ObjectHelper;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.Subject;

/**
 * copy from {@link io.reactivex.subjects.PublishSubject}
 *
 * add the judge logic for whether the action is handled or not, after the {@code onNext} method.
 */
public final class PublishSubject extends Subject<Handle> {
    /** The terminated indicator for the subscribers array. */
    @SuppressWarnings("rawtypes")
    static final PublishDisposable[] TERMINATED = new PublishDisposable[0];
    /** An empty subscribers array to avoid allocating it all the time. */
    @SuppressWarnings("rawtypes")
    static final PublishDisposable[] EMPTY = new PublishDisposable[0];

    /** The array of currently subscribed subscribers. */
    final AtomicReference<PublishDisposable[]> subscribers;

    /** The error, write before terminating and read after checking subscribers. */
    Throwable error;

    /**
     * Constructs a PublishSubject.
     * @return the new PublishSubject
     */
    @CheckReturnValue
    @NonNull
    public static PublishSubject create() {
        return new PublishSubject();
    }

    /**
     * Constructs a PublishSubject.
     * @since 2.0
     */
    @SuppressWarnings("unchecked")
    PublishSubject() {
        subscribers = new AtomicReference<PublishDisposable[]>(EMPTY);
    }

    @Override
    protected void subscribeActual(Observer<? super Handle> t) {
        PublishDisposable ps = new PublishDisposable(t, this);
        t.onSubscribe(ps);
        if (add(ps)) {
            // if cancellation happened while a successful add, the remove() didn't work
            // so we need to do it again
            if (ps.isDisposed()) {
                remove(ps);
            }
        } else {
            Throwable ex = error;
            if (ex != null) {
                t.onError(ex);
            } else {
                t.onComplete();
            }
        }
    }

    /**
     * Tries to add the given subscriber to the subscribers array atomically
     * or returns false if the subject has terminated.
     * @param ps the subscriber to add
     * @return true if successful, false if the subject has terminated
     */
    boolean add(PublishDisposable ps) {
        for (;;) {
            PublishDisposable[] a = subscribers.get();
            if (a == TERMINATED) {
                return false;
            }

            int n = a.length;
            @SuppressWarnings("unchecked")
            PublishDisposable[] b = new PublishDisposable[n + 1];
            System.arraycopy(a, 0, b, 0, n);
            b[n] = ps;

            if (subscribers.compareAndSet(a, b)) {
                return true;
            }
        }
    }

    /**
     * Atomically removes the given subscriber if it is subscribed to the subject.
     * @param ps the subject to remove
     */
    @SuppressWarnings("unchecked")
    void remove(PublishDisposable ps) {
        for (;;) {
            PublishDisposable[] a = subscribers.get();
            if (a == TERMINATED || a == EMPTY) {
                return;
            }

            int n = a.length;
            int j = -1;
            for (int i = 0; i < n; i++) {
                if (a[i] == ps) {
                    j = i;
                    break;
                }
            }

            if (j < 0) {
                return;
            }

            PublishDisposable[] b;

            if (n == 1) {
                b = EMPTY;
            } else {
                b = new PublishDisposable[n - 1];
                System.arraycopy(a, 0, b, 0, j);
                System.arraycopy(a, j + 1, b, j, n - j - 1);
            }
            if (subscribers.compareAndSet(a, b)) {
                return;
            }
        }
    }

    @Override
    public void onSubscribe(Disposable d) {
        if (subscribers.get() == TERMINATED) {
            d.dispose();
        }
    }

    @Override
    public void onNext(Handle t) {
        ObjectHelper.requireNonNull(t, "onNext called with null. Null values are generally not allowed in 2.x operators and sources.");
        for (PublishDisposable pd : subscribers.get()) {
            pd.onNext(t);
        }
        if (!t.getHandled()) {
            Log.e(RxFlux.RxFluxTag, "There has no store register the action " + t.toString());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onError(Throwable t) {
        ObjectHelper.requireNonNull(t, "onError called with null. Null values are generally not allowed in 2.x operators and sources.");
        if (subscribers.get() == TERMINATED) {
            RxJavaPlugins.onError(t);
            return;
        }
        error = t;

        for (PublishDisposable pd : subscribers.getAndSet(TERMINATED)) {
            pd.onError(t);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete() {
        if (subscribers.get() == TERMINATED) {
            return;
        }
        for (PublishDisposable pd : subscribers.getAndSet(TERMINATED)) {
            pd.onComplete();
        }
    }

    @Override
    public boolean hasObservers() {
        return subscribers.get().length != 0;
    }

    @Override
    @Nullable
    public Throwable getThrowable() {
        if (subscribers.get() == TERMINATED) {
            return error;
        }
        return null;
    }

    @Override
    public boolean hasThrowable() {
        return subscribers.get() == TERMINATED && error != null;
    }

    @Override
    public boolean hasComplete() {
        return subscribers.get() == TERMINATED && error == null;
    }

    /**
     * Wraps the actual subscriber, tracks its requests and makes cancellation
     * to remove itself from the current subscribers array.
     */
    static final class PublishDisposable extends AtomicBoolean implements Disposable {

        private static final long serialVersionUID = 3562861878281475070L;
        /** The actual subscriber. */
        final Observer<? super Handle> downstream;
        /** The subject state. */
        final PublishSubject parent;

        /**
         * Constructs a PublishSubscriber, wraps the actual subscriber and the state.
         * @param actual the actual subscriber
         * @param parent the parent PublishProcessor
         */
        PublishDisposable(Observer<? super Handle> actual, PublishSubject parent) {
            this.downstream = actual;
            this.parent = parent;
        }

        public void onNext(Handle t) {
            if (!get()) {
                downstream.onNext(t);
            }
        }

        public void onError(Throwable t) {
            if (get()) {
                RxJavaPlugins.onError(t);
            } else {
                downstream.onError(t);
            }
        }

        public void onComplete() {
            if (!get()) {
                downstream.onComplete();
            }
        }

        @Override
        public void dispose() {
            if (compareAndSet(false, true)) {
                parent.remove(this);
            }
        }

        @Override
        public boolean isDisposed() {
            return get();
        }
    }
}
