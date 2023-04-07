package io.github.kyleescobar.revtools.deobfuscator.transformer

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import io.github.kyleescobar.revtools.asm.tree.nextReal
import io.github.kyleescobar.revtools.deobfuscator.Transformer
import org.objectweb.asm.Opcodes.GOTO
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.tinylog.kotlin.Logger

class RedundantGotoTransformer : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach { method ->
                val insns = method.instructions.iterator()
                while(insns.hasNext()) {
                    val insn = insns.next()
                    if(insn.opcode == GOTO) {
                        insn as JumpInsnNode
                        if(insn.nextReal == insn.label.nextReal) {
                            insns.remove()
                            count++
                        }
                    }
                }
            }
        }

        Logger.info("Removed $count redundant GOTO instructions.")
    }
}