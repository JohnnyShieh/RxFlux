package com.johnny.rxflux

/**
 * description
 *
 * @author Johnny Shieh (JohnnyShieh17@gmail.com)
 * @version 1.0
 *
 * Created on 2019-06-13
 */
object RxFlux {
    const val RxFluxTag = "RxFlux"
    var enableLog = false
        private set

    fun enableRxFluxLog(enabled: Boolean) {
        enableLog = enabled
    }

    inline fun <reified T, reified V> newPairActionType(id: String) =
        ActionType(id, T::class.java, V::class.java)

    inline fun <reified V> newActionType(id: String) =
        ActionType(id, Unit::class.java, V::class.java)

    fun newUnitActionType(id: String) = ActionType(id, Unit::class.java, Unit::class.java)

    fun postAction(
        type: ActionType<Unit, Unit>,
        target: Store? = null
    ) = Dispatcher.postAction(Action(type, target, Unit, Unit))

    fun <V> postAction(
        type: ActionType<Unit, V>,
        target: Store? = null,
        successValue: V
    ) = Dispatcher.postAction(Action(type, target, Unit, successValue))

    fun <T, V> postAction(
        type: ActionType<T, V>,
        target: Store? = null,
        initValue: T,
        successValue: V
    ) = Dispatcher.postAction(Action(type, target, initValue, successValue))

    fun postError(
        type: ActionType<Unit, *>,
        target: Store? = null,
        throwable: Throwable? = null
    ) = Dispatcher.postError(ErrorAction(type, target, Unit, throwable))

    fun <T> postError(
        type: ActionType<T, *>,
        target: Store? = null,
        initValue: T,
        throwable: Throwable? = null
    ) = Dispatcher.postError(ErrorAction(type, target, initValue, throwable))
}