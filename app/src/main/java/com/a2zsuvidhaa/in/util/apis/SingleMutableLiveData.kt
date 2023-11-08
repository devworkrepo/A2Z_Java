package com.a2zsuvidhaa.`in`.util.apis

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleMutableLiveData<T>() : MutableLiveData<T>() {

    constructor(value: T) : this() {
        super.postValue(value)
    }

    private val mPending = AtomicBoolean(false)

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner) {
            if (mPending.compareAndSet(true, false)) observer.onChanged(it)
        }
    }

   /* fun singleObserver(owner: LifecycleOwner, observer: Observer<T>) {
        super.observe(owner, {
            if (mPending.compareAndSet(true, false)) observer.onChanged(it)
        })
    }*/

    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    @MainThread
    override fun getValue(): T? {
        mPending.set(true)
        return super.getValue()
    }

    @MainThread
    fun call() {
        value = null
    }
}