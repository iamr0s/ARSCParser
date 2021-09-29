package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder

class TableChunk {
    companion object {
        const val TYPE: Short = 0x0002

        fun checkType(short: Short) {
            if (short != TYPE) throw Exception("not resources table")
        }
    }

    var stringPool: StringPoolChunk

    var packages: Array<PackageChunk>

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        checkType(wrap.short)
        val headSize = wrap.short
        val chunkSize = wrap.int
        val packageCount = wrap.int
        wrap.position(offset + headSize)

        stringPool = StringPoolChunk(wrap)

        val packages = ArrayList<PackageChunk>()
        for (i in 0 until packageCount) {
            packages.add(PackageChunk(wrap))
        }
        this.packages = packages.toTypedArray()

        wrap.position(offset + chunkSize)

        wrap.order(order)
    }
}