package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder

class Value {
    var res0: Byte

    var type: Byte

    var data: Int

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        val valueSize = wrap.short
        res0 = wrap.get()
        type = wrap.get()
        data = wrap.int

        wrap.position(offset + valueSize)

        wrap.order(order)
    }
}