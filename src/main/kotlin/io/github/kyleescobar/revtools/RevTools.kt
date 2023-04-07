package io.github.kyleescobar.revtools

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.versionOption

object RevTools {

    @JvmStatic
    fun main(args: Array<String>) = CLI().subcommands(
        DeobfuscatorCommand()
    ).main(args)

    private class CLI : NoOpCliktCommand(
        name = "Rev Tools",
        help = """
            Revision updating and deobfuscation tools for the Old School RuneScape gamepack
        """.trimIndent(),
        printHelpOnEmptyArgs = true,
        invokeWithoutSubcommand = false
    ) {

        override fun run() {

        }
    }
}