package io.github.kyleescobar.revtools.deobfuscator

import io.github.kyleescobar.revtools.asm.tree.ClassPool
import org.tinylog.kotlin.Logger
import java.io.File
import kotlin.reflect.full.createInstance

class Deobfuscator(
    val inputJarFile: File,
    val outputJarFile: File,
    val debugMode: Boolean = false,
) {

    lateinit var pool: ClassPool private set

    private val transformers = mutableListOf<Transformer>()

    private fun initTransformers() {
        transformers.clear()

        /**
         * === Register all bytecode transformers here ===
         * *NOTE* The order defined here is the order the transformers run in.
         * ===============================================
         */

        Logger.info("Registered ${transformers.size} bytecode transformers.")
    }

    fun run() {
        Logger.info("Initializing deobfuscator.")

        /*
         * Register transformers.
         */
        this.initTransformers()

        /*
         * Initialization steps
         */
        pool = ClassPool.fromJar(inputJarFile)
        pool.computeHierarchy()
        Logger.info("Loaded ${pool.classes.size} classes from input jar into class pool.")

        /*
         * Start deobfuscation. Run each registered transformer.
         */
        Logger.info("Starting deobfuscator.")

        val start = System.currentTimeMillis()
        transformers.forEach { transformer ->
            Logger.info("Running transformer \"${transformer::class.simpleName}\".")
            transformer.run(pool)
        }
        val delta = System.currentTimeMillis() - start
        Logger.info("Completed all bytecode transformers in ${delta}ms.")

        /*
         * Save the deobfuscated classes in the pool to the output jar file.
         */
        Logger.info("Saving deobfuscated classes to output jar.")
        pool.toJar(outputJarFile, skipIgnored = true)
        Logger.info("Successfully wrote ${pool.classes.size} classes to output jar.")

        /*
         * If debug mode is enabled, run the test client using the output deobfuscated jar.
         */
        if(debugMode) {
            Logger.info("Debugging mode is enabled. Starting test client.")
            DebugClient(outputJarFile, inputJarFile).start()
        }

        Logger.info("Deobfuscator has completed successfully.")
    }

    private inline fun <reified T : Transformer> register() {
        transformers.add(T::class.createInstance())
    }
}