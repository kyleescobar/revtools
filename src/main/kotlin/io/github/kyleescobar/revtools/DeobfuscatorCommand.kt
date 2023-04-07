package io.github.kyleescobar.revtools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.check
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import io.github.kyleescobar.revtools.deobfuscator.Deobfuscator
import org.tinylog.kotlin.Logger

class DeobfuscatorCommand : CliktCommand(
    name = "deobfuscate",
    help = "Deobfuscates the vanilla gamepack from Jagex",
    printHelpOnEmptyArgs = true
) {

    private val inputJarFile by argument(
        name = "Input Jar",
        help = "Path of the obfuscated gamepack jar"
    ).file(mustExist = true, canBeDir = false).check {
        it.extension == "jar"
    }

    private val outputJarFile by argument(
        name = "Output Jar",
        help = "Path to save the resulting deobfuscated gamepack jar"
    ).file(canBeDir = false).validate {
        if(it.exists()) {
            it.deleteRecursively()
        }
    }

    private val debugMode by option(
        "--debug", "-d",
        help = "Enables the post deobfuscation test client running from output jar"
    ).flag(default = false)

    override fun run() {
        Deobfuscator(inputJarFile, outputJarFile, debugMode).run()
    }
}