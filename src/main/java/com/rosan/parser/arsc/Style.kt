package com.rosan.parser.arsc

class Style(var index: Int, var firstChar: Int, var lastChar: Int) {
    override fun toString(): String {
        return "${super.toString()} {index=$index, firstChar=$firstChar, lastChar=$lastChar}"
    }
}