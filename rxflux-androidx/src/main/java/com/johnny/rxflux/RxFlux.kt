package com.johnny.rxflux

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2019-06-13
 */
internal var enableLog = false
    private set

fun enableRxFluxLog(enabled: Boolean) {
    enableLog = enabled
}

fun postAction(type: String, singleObj: Any? = null, store: Store? = null) = Dispatcher.instance.postAction(Action(type).apply {
    singleData = singleObj
    target = store
})

fun postAction(type: String, vararg params: Pair<String, Any>, store: Store? = null) {
    val action = Action(type)
    params.forEach {
        action.data[it.first] = it.second
    }
    action.target = store
    Dispatcher.instance.postAction(action)
}

fun postError(type: String, throwable: Throwable? = null, singleObj: Any? = null, store: Store? = null) = Dispatcher.instance.postError(Action(type, throwable).apply {
    singleData = singleObj
    target = store
})

fun postError(type: String, throwable: Throwable? = null, vararg params: Pair<String, Any>, store: Store? = null) {
    val action = Action(type, throwable)
    params.forEach {
        action.data[it.first] = it.second
    }
    action.target = store
    Dispatcher.instance.postError(action)
}