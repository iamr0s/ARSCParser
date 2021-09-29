package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and


class TypeChunk {
    companion object {
        var a = false
        const val TYPE: Short = 0x0201

        const val FLAG_SPARSE: Byte = 0x01

        fun checkType(short: Short) {
            if (short != TYPE) throw Exception("not resources type spec")
        }

        fun isSparse(byte: Byte): Boolean {
            return byte and FLAG_SPARSE != 0.toByte()
        }
    }

    var typeId: Byte

    var keyIndex: Int

    var flags: Byte

    var reserved: Short

    var entryCount: Int

    var entriesOffset: Int

    var config: TypeConfig

    var entries: Map<Int, Entry>? = null

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
        flags = wrap.get()
        reserved = wrap.short
        entryCount = wrap.int
        entriesOffset = wrap.int
        config = TypeConfig(wrap)
        wrap.position(offset + headerSize)

        val entryOffsets = HashMap<Int, Int>()
        if (isSparse(flags)) {
            for (i in 0 until entryCount) {
                entryOffsets[wrap.short.toInt()] = wrap.short * 4
            }
        } else {
            for (i in 0 until entryCount) {
                entryOffsets[i] = wrap.int
            }
        }

        val entries = HashMap<Int, Entry>()
        for (entryId in entryOffsets.keys) {
            val entryOffset = entryOffsets.getOrElse(entryId) { -1 }
            if (entryOffset == -1) continue
            wrap.position(offset + entriesOffset + entryOffset)
            entries[entryId] = Entry(wrap)
        }
        this.entries = entries

        wrap.position(offset + chunkSize)

        wrap.order(order)
    }
}