package de.flapdoodle.kfx.converters

sealed class ValueOrError<T: Any> {
  abstract fun <M: Any> mapValue(mapper: (T) -> M): ValueOrError<M>

  data class Value<T: Any>(val value: T): ValueOrError<T>() {
    override fun <M : Any> mapValue(mapper: (T) -> M): ValueOrError<M> {
      try {
        return Value(mapper(value))
      } catch (ex: Exception) {
        return Error(ex)
      }
    }
  }
  
  data class Error<T: Any>(val exception: Exception): ValueOrError<T>() {
    override fun <M : Any> mapValue(mapper: (T) -> M): ValueOrError<M> {
      return Error(exception)
    }
  }
}