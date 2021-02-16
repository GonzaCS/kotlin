/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package generators.unicode.specialMappings

import generators.unicode.SpecialCasingGenerator
import generators.unicode.SpecialCasingLine
import generators.unicode.UnicodeDataLine
import generators.unicode.specialMappings.builders.LowercaseSpecialMappingsBuilder
import generators.unicode.specialMappings.builders.SpecialMappingsBuilder
import generators.unicode.specialMappings.builders.TitlecaseSpecialMappingsBuilder
import generators.unicode.specialMappings.builders.UppercaseSpecialMappingsBuilder
import generators.unicode.specialMappings.writers.LowercaseSpecialMappingsWriter
import generators.unicode.specialMappings.writers.SpecialMappingsWriter
import generators.unicode.specialMappings.writers.TitlecaseSpecialMappingsWriter
import generators.unicode.specialMappings.writers.UppercaseSpecialMappingsWriter
import generators.unicode.ranges.RangesWritingStrategy
import generators.unicode.writeHeader
import templates.KotlinTarget
import java.io.File
import java.io.FileWriter

internal class SpecialMappingsGenerator private constructor(
    private val outputFile: File,
    private val mappingsBuilder: SpecialMappingsBuilder,
    private val mappingsWriter: SpecialMappingsWriter
) : SpecialCasingGenerator {
    override fun appendLine(line: SpecialCasingLine) {
        mappingsBuilder.append(line)
    }

    override fun close() {
        val mappings = mappingsBuilder.build()

        FileWriter(outputFile).use { writer ->
            writer.writeHeader(outputFile, "kotlin.text")
            writer.appendLine()
            writer.appendLine("// ${mappings.size} mappings totally")

            mappingsWriter.write(mappings, writer)
        }
    }


    companion object {
        fun forUppercase(outputFile: File, target: KotlinTarget, unicodeDataLines: List<UnicodeDataLine>): SpecialMappingsGenerator {
            val builder = UppercaseSpecialMappingsBuilder(unicodeDataLines)
            val writer = UppercaseSpecialMappingsWriter(RangesWritingStrategy.of(target, "Uppercase"))
            return SpecialMappingsGenerator(outputFile, builder, writer)
        }

        fun forLowercase(outputFile: File, target: KotlinTarget, unicodeDataLines: List<UnicodeDataLine>): SpecialMappingsGenerator {
            val builder = LowercaseSpecialMappingsBuilder(unicodeDataLines)
            val writer = LowercaseSpecialMappingsWriter(RangesWritingStrategy.of(target, "Lowercase"))
            return SpecialMappingsGenerator(outputFile, builder, writer)
        }

        fun forTitlecase(outputFile: File, target: KotlinTarget, unicodeDataLines: List<UnicodeDataLine>): SpecialMappingsGenerator {
            val builder = TitlecaseSpecialMappingsBuilder(unicodeDataLines)
            val writer = TitlecaseSpecialMappingsWriter(RangesWritingStrategy.of(target, "Titlecase"))
            return SpecialMappingsGenerator(outputFile, builder, writer)
        }
    }
}
