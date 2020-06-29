package com.yu.retrofitlib.gson.adapter

import android.text.TextUtils
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.yu.retrofitlib.util.Utils.isNumericFloatingPoint
import java.io.IOException

/**
 * Created by yu on 2020-06-28.
 * Integer解析器
 */
class IntegerTypeAdapter : TypeAdapter<Int?>() {
    @Throws(IOException::class)
    override fun write(out: JsonWriter, value: Int?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(value)
        }
    }

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Int? {
        return when (val peek = reader.peek()) {
            JsonToken.NULL -> {
                reader.nextNull()
                0
            }
            JsonToken.NUMBER -> reader.nextInt()
            JsonToken.STRING -> {
                val numberStr = reader.nextString()
                if (TextUtils.isEmpty(numberStr.trim { it <= ' ' })) {
                    return 0
                }
                //返回的numberStr不会为null
                if (isNumericFloatingPoint(numberStr)) {
                    numberStr.toDouble().toInt()
                } else {
                    Integer.valueOf(numberStr)
                }
            }
            else -> throw IllegalStateException("Expected NUMBER or String but was $peek")
        }
    }
}