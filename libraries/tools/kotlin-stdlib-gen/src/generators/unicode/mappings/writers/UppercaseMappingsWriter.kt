/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package generators.unicode.mappings.writers

import generators.unicode.mappings.patterns.EqualDistanceMappingPattern
import generators.unicode.mappings.patterns.MappingPattern
import generators.unicode.ranges.RangesWritingStrategy
import generators.unicode.ranges.writers.writeIntArray
import java.io.FileWriter

internal class UppercaseMappingsWriter(private val strategy: RangesWritingStrategy) : MappingsWriter {
    override fun write(mappings: List<MappingPattern>, writer: FileWriter) {
        @Suppress("UNCHECKED_CAST")
        val distanceMappings = mappings as List<EqualDistanceMappingPattern>

        val start = distanceMappings.map { it.start }
        val length = distanceMappings.map { (it.mapping shl 12) or (it.distance shl 8) or it.length }

        strategy.beforeWritingRanges(writer)
        writer.writeIntArray("rangeStart", start, strategy)
        writer.appendLine()
        writer.writeIntArray("rangeLength", length, strategy)
        strategy.afterWritingRanges(writer)
        writer.appendLine()
        writer.appendLine(equalDistanceMapping())
        writer.appendLine()
        writer.appendLine(uppercaseCharImpl())
    }

    private fun equalDistanceMapping(): String = """
        internal fun equalDistanceMapping(code: Int, start: Int, pattern: Int): Char {
            val diff = code - start

            val length = pattern and 0xff
            if (diff >= length) {
                return code.toChar()
            }

            val distance = (pattern shr 8) and 0xf
            if (diff % distance != 0) {
                return code.toChar()
            }

            val mapping = pattern shr 12
            return (code + mapping).toChar()
        }
    """.trimIndent()

    private fun uppercaseCharImpl(): String = """
        internal fun Char.uppercaseCharImpl(): Char {
            val code = this.toInt()
            val index = binarySearchRange(rangeStart, code)
            return equalDistanceMapping(code, rangeStart[index], rangeLength[index])
        }
    """.trimIndent()
}