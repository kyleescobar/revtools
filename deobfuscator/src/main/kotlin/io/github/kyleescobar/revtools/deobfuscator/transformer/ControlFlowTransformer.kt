package io.github.kyleescobar.revtools.deobfuscator.transformer

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import io.github.kyleescobar.revtools.deobfuscator.Transformer
import io.github.kyleescobar.revtools.asm.attribute.OrigInfoAttribute
import org.objectweb.asm.Attribute

class ControlFlowTransformer : Transformer {

    override fun run(pool: ClassPool) {
        val attr = OrigInfoAttribute("client", "init", "()V")
        val cls = pool.findClass("client")!!
        cls.attrs = mutableListOf<Attribute>(attr)
        println("Saved attr")
    }
}