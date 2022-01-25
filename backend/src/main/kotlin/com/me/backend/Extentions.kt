package com.me.backend

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.me.backend.model.OptionalInputUndefinedException
import java.util.*

fun <T> OptionalInput<T>.ifDefinedOrNull(callback: (it: T?) -> Unit) {
    if (this is OptionalInput.Defined) {
        callback(this.value)
    }
}

fun <T> OptionalInput<T>.ifDefined(callback: (it: T) -> Unit) {
    if (this is OptionalInput.Defined && this.value != null) {
        callback(this.value!!)
    }
}

fun <T> OptionalInput<T>.toOptional()= if (this is OptionalInput.Defined) Optional.of(this.value!!) else Optional.empty()
fun OptionalInput<UUID?>.toOptionalOrZero()= if (this is OptionalInput.Defined) Optional.of(this.value ?: zeroID) else Optional.empty()

val UUID.isZero get() = mostSignificantBits == 0L && leastSignificantBits == 0L
/**
 * @throws OptionalInputUndefinedException when the value is undefined
 */
fun <T> OptionalInput<T>.getOrNull(): T? {
    if (this is OptionalInput.Defined && this.value != null) {
        return this.value
    } else {
        throw OptionalInputUndefinedException()
    }
}
/**
 * @throws OptionalInputUndefinedException when the value is undefined
 */
fun <T> OptionalInput<T>.get(): T {
    if (this is OptionalInput.Defined && this.value != null) {
        return this.value!!
    } else {
        throw OptionalInputUndefinedException()
    }
}
