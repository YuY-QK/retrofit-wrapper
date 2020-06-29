package com.yu.retrofitlib.gson.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException

/**
 * Created by yu on 2020-06-28
 * Boolean解析器
 */
class BooleanTypeAdapter : TypeAdapter<Boolean?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Boolean?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    //in.nextString().equalsIgnoreCase("1")，如果想转换字符串"1"
    @Throws(IOException::class)
    override fun read(reader: JsonReader): Boolean? {
        return when (val peek = reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                false
            }
            JsonToken.NUMBER -> reader.nextInt() == 1
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.STRING -> java.lang.Boolean.parseBoolean(reader.nextString())
            else -> throw IllegalStateException("Expected BOOLEAN or NUMBER but was $peek")
        }
    }
}