/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package generators.unicode.specialMappings.builders

import generators.unicode.SpecialCasingLine
import generators.unicode.UnicodeDataLine
import generators.unicode.hexToInt

internal abstract class SpecialMappingsBuilder(unicodeDataLines: List<UnicodeDataLine>) {
    private val unicodeDataLines = unicodeDataLines.associateBy { it.char.hexToInt() }
    private val mappings = mutableMapOf<Int, List<String>>()

    fun append(line: SpecialCasingLine) {
        if (line.conditionList.isNotEmpty()) return

        val charCode = line.char.hexToInt()

        check(charCode <= Char.MAX_VALUE.toInt()) { "Handle special casing for the supplementary code point: $line" }

        val mapping = mapping(charCode, line) ?: return

        mappings[charCode] = mapping
    }

    fun build(): Map<Int, List<String>> {
//        println(mappings)
//        println("${this.javaClass} # ${mappings.size}")
        return mappings
    }

    private fun mapping(charCode: Int, line: SpecialCasingLine): List<String>? {
        val mapping = line.mapping()

        check(mapping.isNotEmpty() && mapping.all { it.isNotEmpty() })

        if (mapping.size == 1) {
            val specialCasingMapping = mapping.first()

            val unicodeLine = unicodeDataLines[charCode]
            val unicodeDataMapping = unicodeLine?.mapping()?.takeIf { it.isNotEmpty() } ?: line.char

            check(unicodeDataMapping == specialCasingMapping) {
                "UnicodeData.txt and SpecialCasing.txt files have different single char case conversion"
            }

            return null
        }

        return mapping
    }

    abstract fun SpecialCasingLine.mapping(): List<String>
    abstract fun UnicodeDataLine.mapping(): String
}