package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder

class StringPoolChunk {

    companion object {
        const val TYPE: Short = 0x0001

        const val FLAG_UTF8: Int = 0x00000100

        const val FLAG_SORTED: Int = 0x00000001

        fun checkType(short: Short) {
            if (short != TYPE) throw Exception("not resources string pool")
        }

        fun isUTF8(flags: Int): Boolean {
            return flags and FLAG_UTF8 != 0
        }
    }

    val strings: Array<String?>

    val styles: Array<Style?>

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        checkType(wrap.short)

        val headSize = wrap.short
        val chunkSize = wrap.int
        val stringCount = wrap.int
        val styleCount = wrap.int
        val flags = wrap.int
        val stringOffset = wrap.int
        val styleOffset = wrap.int

        wrap.position(offset + headSize)

        val stringOffsets = IntArray(stringCount)
        val styleOffsets = IntArray(styleCount)
        for (i in 0 until stringCount) {
            stringOffsets[i] = wrap.int
        }
        for (i in 0 until styleCount) {
            styleOffsets[i] = wrap.int
        }

        strings = Array<String?>(stringCount) { null }
        for (i in 0 until stringCount) {
            wrap.position(offset + stringOffset + stringOffsets[i])
            strings[i] = if (isUTF8(flags)) {
                wrap.get()
                val length = wrap.get().toInt()
                val bytes = if (length > 0) {
                    val bytes = ByteArray(length)
                    wrap.get(bytes)
                    bytes
                } else {
                    val bytes = ArrayList<Byte>()
                    do {
                        bytes.add(wrap.get())
                    } while (bytes.last() != 0x00.toByte())
                    bytes.toByteArray()
                }
                bytes.toString(Charsets.UTF_8)
            } else {
                val length = wrap.short.toInt() * 2
                val bytes = if (false) {
                    val bytes = ByteArray(length)
                    wrap.get(bytes)
                    bytes
                } else {
                    val bytes = ArrayList<Byte>()
                    do {
                        bytes.add(wrap.get())
                    } while (bytes.getOrNull(bytes.lastIndex - 1) != 0x00.toByte() || bytes.last() != 0x00.toByte())
                    bytes.removeLast()
                    bytes.toByteArray()
                }
                bytes.toString(Charsets.UTF_16LE)
            }
        }

        styles = Array<Style?>(styleCount) { null }
        for (i in 0 until styleCount) {
            wrap.position(offset + styleOffset + styleOffsets[i])
            val index = wrap.int
            val firstChar = wrap.int
            val lastChar = wrap.int
            styles[i] = Style(index, firstChar, lastChar)
        }

        wrap.position(offset + chunkSize)

        wrap.order(order)
    }
}