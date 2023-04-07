package io.github.kyleescobar.revtools.deobfuscator.transformer

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import io.github.kyleescobar.revtools.asm.tree.THROW_RETURN_OPCODES
import io.github.kyleescobar.revtools.asm.tree.isPure
import io.github.kyleescobar.revtools.asm.tree.nextReal
import io.github.kyleescobar.revtools.deobfuscator.Transformer
import org.objectweb.asm.Opcodes.ATHROW
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.tinylog.kotlin.Logger

class RuntimeExceptionTransformer : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach { method ->
                val tcbs = method.tryCatchBlocks.iterator()
                while(tcbs.hasNext()) {
                    val tcb = tcbs.next()
                    if(tcb.type == "java/lang/RuntimeException") {
                        var insn: AbstractInsnNode? = tcb.handler
                        while(insn!!.opcode != ATHROW) {
                            val next = insn.next
                            method.instructions.remove(insn)
                            insn = next
                        }
                        method.instructions.remove(insn)
                        tcbs.remove()
                        count++
                    }
                }
            }
        }

        Logger.info("Removed $count RuntimeException try-catch blocks.")
    }
}