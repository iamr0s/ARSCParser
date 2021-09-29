package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder

class TypeSpecChunk {
    companion object {
        const val TYPE: Short = 0x0202

        fun checkType(short: Short) {
            if (short != TYPE) throw Exception("not resources type spec")
        }

    }

    val typeId: Byte

    val keyIndex: Int

    val res0: Byte

    val res1: Short

    val configMask: IntArray

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        checkType(wrap.short)
        val headerSize = wrap.short
        val chunkSize = wrap.int
        typeId = wrap.get()
        keyIndex = typeId - 1
        res0 = wrap.get() // must be 0x00
        res1 = wrap.short // must be 0x0000
        val entryCount = wrap.int
        wrap.position(offset + headerSize)

        configMask = IntArray(entryCount)
        for (i in 0 until entryCount) {
            configMask[i] = wrap.int
        }

        wrap.position(offset + chunkSize)

        wrap.order(order)
    }
}