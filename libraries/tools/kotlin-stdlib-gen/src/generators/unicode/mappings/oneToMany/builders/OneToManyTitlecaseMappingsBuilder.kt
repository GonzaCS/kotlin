/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package generators.unicode.mappings.oneToMany.builders

import generators.unicode.SpecialCasingLine
import generators.unicode.UnicodeDataLine

internal class OneToManyTitlecaseMappingsBuilder(unicodeDataLines: List<UnicodeDataLine>) : OneToManyMappingsBuilder(unicodeDataLines) {
    override fun SpecialCasingLine.mapping(): List<String> = titlecaseMapping
    override fun UnicodeDataLine.mapping(): String = titlecaseMapping
}
