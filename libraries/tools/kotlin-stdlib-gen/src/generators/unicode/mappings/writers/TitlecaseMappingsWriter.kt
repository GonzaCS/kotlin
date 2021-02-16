/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package generators.unicode.mappings.writers

import generators.unicode.mappings.patterns.EqualDistanceMappingPattern
import generators.unicode.mappings.patterns.LuLtLlMappingPattern
import generators.unicode.mappings.patterns.MappingPattern
import generators.unicode.ranges.writers.toHexIntLiteral
import java.io.FileWriter

internal class TitlecaseMappingsWriter : MappingsWriter {

    override fun write(mappings: List<MappingPattern>, writer: FileWriter) {
        val LuLtLlMappings = mappings.filterIsInstance<LuLtLlMappingPattern>()
        val zeroMappings = mappings.filterIsInstance<EqualDistanceMappingPattern>().filter { it.distance == 1 && it.mapping == 0 }

        check(LuLtLlMappings.size + zeroMappings.size == mappings.size) { "Handle new types of titlecase mapping." }
        check(LuLtLlMappings.all { it.start % 3 == 2 }) { "Handle when code of the Lt char is not multiple of 3." }

        writer.append(
            """
            internal fun Char.titlecaseCharImpl(): Char {
                val code = this.toInt()
                if (${rangeChecks(LuLtLlMappings, "code")}) {
                    return (3 * ((code + 1) / 3)).toChar()
                }
                if (${rangeChecks(zeroMappings, "code")}) {
                    return this
                }
                return uppercaseCharImpl()
            }
            """.trimIndent()
        )
    }

    private fun rangeChecks(mappings: List<MappingPattern>, code: String): String {
        return mappings.joinToString(separator = " || ") { "$code in ${it.start.toHexIntLiteral()}..${it.end.toHexIntLiteral()}" }
    }
}