package ru.g0rd1.peoplesfinder.model

sealed class Optional<T> {
    class Empty<T> : Optional<T>()
    data class Value<T>(val value: T) : Optional<T>()

    companion object {
        fun <T> create(value: T?): Optional<T> {
            value ?: return Empty()
            return Value(value)
        }

        fun <T> empty(): Optional<T> {
            return Empty()
        }
    }
}