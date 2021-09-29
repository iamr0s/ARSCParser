package com.rosan.parser.arsc

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import kotlin.experimental.and

class TypeConfig {
    companion object {
        /*
        * copy from Apktool
        * */
        private fun unpackLanguageOrRegion(in0: Byte, in1: Byte, base: Char): ByteArray {
            // check high bit, if so we have a packed 3 letter code
            val in0 = in0.toInt()
            val in1 = in1.toInt()
            if (in0 shr 7 and 1 == 1) {
                val first: Int = in1 and 0x1F
                val second: Int = (in1 and 0xE0 shr 5) + (in0 and 0x03 shl 3)
                val third: Int = in0 and 0x7C shr 2

                // since this function handles languages & regions, we add the value(s) to the base char
                // which is usually 'a' or '0' depending on language or region.
                return byteArrayOf((first + base.toInt()).toByte(),
                    (second + base.toInt()).toByte(),
                    (third + base.toInt()).toByte())
            }
            return byteArrayOf(in0.toByte(), in1.toByte())
        }

        fun asciiString(bytes: ByteArray): String {
            val builder = StringBuilder()
            for (i in bytes.indices) {
                val byte = bytes[i]
                if (byte != 0x0.toByte()) builder.append(byte.toChar()) else break
            }
            return builder.toString()
        }

        fun unpackLanguage(language: ByteArray): String {
            return asciiString(unpackLanguageOrRegion(language[0], language[1], 'a'))
        }

        fun unpackCountry(country: ByteArray): String {
            return asciiString(unpackLanguageOrRegion(country[0], country[1], '0'))
        }
    }

    var mcc: Short

    var mnc: Short

    var language: ByteArray

    var country: ByteArray

    var orientation: Byte

    var touchscreen: Byte

    var density: Short

    var keyboard: Byte

    var navigation: Byte

    var inputFlags: Byte

    var inputPad0: Byte

    var screenWidth: Short

    var screenHeight: Short

    var sdkVersion: Short

    var minorVersion: Short

    var screenLayout: Byte = 0

    var uiMode: Byte = 0

    var smallestScreenWidthDp: Short = 0

    var screenWidthDp: Short = 0

    var screenHeightDp: Short = 0

    var localeScript = ByteArray(4)

    var localeVariant = ByteArray(8)

    var screenLayout2: Byte = 0

    var colorMode: Byte = 0

    val locale: Locale

    constructor(bytes: ByteArray) : this(ByteBuffer.wrap(bytes))

    constructor(wrap: ByteBuffer) {
        val order = wrap.order()
        wrap.order(ByteOrder.LITTLE_ENDIAN)

        var offset = wrap.position()
        val configSize = wrap.int
        if (configSize < 28) throw Exception("config size < 28")
        mcc = wrap.short
        mnc = wrap.short
        language = ByteArray(2).apply { wrap.get(this) }
        country = ByteArray(2).apply { wrap.get(this) }
        orientation = wrap.get()
        touchscreen = wrap.get()
        density = wrap.short
        keyboard = wrap.get()
        navigation = wrap.get()
        inputFlags = wrap.get()
        inputPad0 = wrap.get()
        screenWidth = wrap.short
        screenHeight = wrap.short
        sdkVersion = wrap.short
        minorVersion = wrap.short

        if (configSize >= 32) {
            screenLayout = wrap.get()
            uiMode = wrap.get()
            smallestScreenWidthDp = wrap.short
        }

        if (configSize >= 36) {
            screenWidthDp = wrap.short
            screenHeightDp = wrap.short
        }

        if (configSize >= 48) {
            wrap.get(localeScript)
            wrap.get(localeVariant)
        }

        if (configSize >= 52) {
            screenLayout2 = wrap.get()
            colorMode = wrap.get()
        }

        // more byte,i don't care

        locale =
            Locale.Builder().setLanguage(unpackLanguage(language)).setRegion(unpackCountry(country))
                .setScript(asciiString(localeScript))
                .setVariant(asciiString(localeVariant))
                .build()

        wrap.position(offset + configSize)

        wrap.order(order)
    }
}