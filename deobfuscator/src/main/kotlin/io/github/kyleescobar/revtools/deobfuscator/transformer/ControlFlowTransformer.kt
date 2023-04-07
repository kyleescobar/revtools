package io.github.kyleescobar.revtools.deobfuscator.transformer

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import io.github.kyleescobar.revtools.deobfuscator.Transformer
import io.github.kyleescobar.revtools.asm.attribute.OrigInfoAttribute
import io.github.kyleescobar.revtools.asm.tree.findMethod
import io.github.kyleescobar.revtools.asm.tree.origInfoAttr
import io.github.kyleescobar.revtools.asm.tree.toByteArray
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.commons.LocalVariablesSorter
import org.objectweb.asm.tree.MethodNode

class ControlFlowTransformer : Transformer {

    override fun run(pool: ClassPool) {

    }
}