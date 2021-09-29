package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder

class PackageChunk {
    companion object {
        const val TYPE: Short = 0x0200

        fun checkType(short: Short) {
            if (short != TYPE) throw Exception("not resources package")
        }
    }

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    var packageId: Int

    var packageName: String

    var typeNameStrings: StringPoolChunk

    var entryNameStrings: StringPoolChunk

    var typeSpecs: Array<TypeSpecChunk>

    var types: Array<TypeChunk>

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        checkType(wrap.short)
        val headSize = wrap.short
        val chunkSize = wrap.int
        packageId = wrap.int
        packageName = ByteArray(256).apply { wrap.get(this) }.toString(Charsets.UTF_8)
        val typeNameStringsOffset = wrap.int
        val typeNameStringsCount = wrap.int
        val entryNameStringsOffset = wrap.int
        val entryNameStringsCount = wrap.int

        wrap.position(offset + headSize)

        wrap.position(offset + typeNameStringsOffset)
        typeNameStrings = StringPoolChunk(wrap)

        wrap.position(offset + entryNameStringsOffset)
        entryNameStrings = StringPoolChunk(wrap)

        val typeSpecs = ArrayList<TypeSpecChunk>()
        val types = ArrayList<TypeChunk>()
        while (wrap.position() < wrap.limit()) {
            val letOffset = wrap.position()
            val type = wrap.short
            val headerSize = wrap.short
            val chunkSize = wrap.int
            wrap.position(letOffset)
            when (type) {
                TypeSpecChunk.TYPE -> {
                    typeSpecs.add(TypeSpecChunk(wrap))
                }
                TypeChunk.TYPE -> {
                    types.add(TypeChunk(wrap))
                }
                else -> {
                    wrap.position(letOffset + chunkSize)
                }
            }
        }
        this.typeSpecs = typeSpecs.toTypedArray()
        this.types = types.toTypedArray()

        wrap.position(offset + chunkSize)

        wrap.order(order)
    }
}