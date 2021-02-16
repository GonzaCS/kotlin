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
        if (line.conditionList.isNotEmpty()) {
            val isLocaleAgnosticCondition = line.conditionList.all { it.length > 2 }
            if (isLocaleAgnosticCondition && (line.char != "03A3"
                        || line.lowercaseMapping != listOf("03C2")
                        || line.uppercaseMapping != listOf("03A3")
                        || line.titlecaseMapping != listOf("03A3")
                        || line.conditionList != listOf("Final_Sigma"))
            ) {
                error("The locale-agnostic conditional mapping $line is not handled. Please update String case conversions implementation.")
            }
            return
        }

        val charCode = line.char.hexToInt()
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

        if (mapping.size <= 1) {
            val specialCasingMapping = mapping.firstOrNull() ?: line.char

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