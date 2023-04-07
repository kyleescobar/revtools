package io.github.kyleescobar.revtools.deobfuscator

import io.github.kyleescobar.revtools.asm.tree.ClassPool

interface Transformer {
    fun run(pool: ClassPool)
}