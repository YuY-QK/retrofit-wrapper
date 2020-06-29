package com.yu.retrofitlib.gson.adapter

import android.text.TextUtils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Created by yu on 2020-06-28
 * Double解析器
 */
class DoubleTypeAdapter : TypeAdapter<Double?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Double?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Double? {
        return when (val peek = reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0.0
            }
            JsonToken.NUMBER -> reader.nextDouble()
            JsonToken.STRING -> {
                val value = reader.nextString()
                if (TextUtils.isEmpty(value.trim { it <= ' ' })) {
                    0.0
                } else java.lang.Double.valueOf(value)
            }
            else -> throw IllegalStateException("Expected NUMBER but was $peek")
        }
    }
}