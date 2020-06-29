package com.yu.retrofitlib.gson.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.yu.retrofitlib.util.Utils.isEmpty
import java.io.IOException

/**
 * Created by yu on 2020-06-28
 * Float解析器
 */
class FloatTypeAdapter : TypeAdapter<Float?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Float?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Float {
        return when (val peek = reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0f
            }
            JsonToken.NUMBER -> reader.nextDouble().toString().toFloat()
            JsonToken.STRING -> {
                val numberStr = reader.nextString()
                if (isEmpty(numberStr)) {
                    0f
                } else {
                    numberStr.toFloat()
                }
            }
            else -> throw IllegalStateException("Expected NUMBER but was $peek")
        }
    }
}