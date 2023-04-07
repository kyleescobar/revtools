package io.github.kyleescobar.revtools.deobfuscator.transformer

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import io.github.kyleescobar.revtools.asm.tree.isBodyEmpty
import io.github.kyleescobar.revtools.deobfuscator.Transformer
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.analysis.Analyzer
import org.objectweb.asm.tree.analysis.BasicInterpreter
import org.tinylog.kotlin.Logger

class DeadCodeTransformer : Transformer {

    private var count = 0

    override fun run(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach { method ->
                var changed: Boolean
                do {
                    changed = false
                    val frames = Analyzer(BasicInterpreter()).analyze(cls.name, method)
                    val insns = method.instructions.iterator()
                    var i = 0
                    for(insn in insns) {
                        if(frames[i++] != null) {
                            continue
                        }
                        insns.remove()
                        count++
                        changed = true
                    }
                    changed = changed or method.tryCatchBlocks.removeIf { it.isBodyEmpty() }
                } while(changed)
            }
        }

        Logger.info("Removed $count dead-code instructions.")
    }
}