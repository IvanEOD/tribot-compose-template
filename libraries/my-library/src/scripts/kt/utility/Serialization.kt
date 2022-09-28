package scripts.kt.utility

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/* Written by IvanEOD 9/13/2022, at 8:36 PM */
class StateTypAdapterFactory : TypeAdapterFactory {

    override fun <T : Any> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val cls = type.rawType

        // Short circuit if this isn't a jetpack compose state object
        if (!State::class.java.isAssignableFrom(cls)) {
            return null
        }

        val typeParams: Array<Type> = (type.type as ParameterizedType).actualTypeArguments
        val param = typeParams[0]
        val delegate = gson.getAdapter(TypeToken.get(param))

        return StateTypeAdapter(delegate) as TypeAdapter<T>
    }
}

// Is your mind blown?
class StateTypeAdapter<I, T, S : State<T>>(private val delegate: TypeAdapter<I>) : TypeAdapter<S>() where T : I {
    override fun write(out: JsonWriter?, value: S) = delegate.write(out, value.value)
    override fun read(reader: JsonReader): S {
        return mutableStateOf(delegate.read(reader)) as S
    }
}

inline fun <reified T> toStateJson(value: T) : String {
    val gson = GsonBuilder()
        .registerTypeAdapterFactory(StateTypAdapterFactory())
        .create()
    return gson.toJson(value)
}

inline fun <reified T> fromStateJson(json: String) : T {
    val gson = GsonBuilder()
        .registerTypeAdapterFactory(StateTypAdapterFactory())
        .create()
    return gson.fromJson(json, T::class.java)
}

inline fun <reified T> deepCopy(ob: T): T {
    val gson = GsonBuilder()
        .registerTypeAdapterFactory(StateTypAdapterFactory())
        .create()
    return gson.fromJson(gson.toJson(ob), T::class.java)
}