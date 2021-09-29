package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and

class Entry {
    companion object {
        const val FLAG_COMPLEX: Short = 0x0001

        fun isComplex(flags: Short): Boolean {
            return flags and FLAG_COMPLEX != 0.toShort()
        }
    }

    var flags: Short

    var keyIndex: Int

    var value: Value? = null

    var values: Map<Int, Value>? = null

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        val entrySize = wrap.short
        flags = wrap.short
        keyIndex = wrap.int

        if (isComplex(flags)) {
            val parent = wrap.int
            val count = wrap.int
            val values = HashMap<Int, Value>()
            for (i in 0 until count) {
                val resId = wrap.int
                val value = Value(wrap)
                values[resId] = value
            }
            this.values = values
        } else {
            value = Value(wrap)
        }

        wrap.position(offset + entrySize)

        wrap.order(order)
    }


}