package io.github.kyleescobar.revtools.logger

import org.fusesource.jansi.Ansi
import org.tinylog.Level
import org.tinylog.core.LogEntryValue
import java.text.SimpleDateFormat
import java.util.*


class BasicWriter @Suppress("unused") constructor(properties: Map<String?, String?>?) :
    org.tinylog.writers.Writer {

    override fun getRequiredLogEntryValues(): MutableCollection<LogEntryValue> {
        return EnumSet.of(
            LogEntryValue.DATE, LogEntryValue.LEVEL, LogEntryValue.THREAD, LogEntryValue.CLASS,
            LogEntryValue.MESSAGE)
    }

    override fun write(logEntry: org.tinylog.core.LogEntry) {
        if (logEntry.getLevel().ordinal < org.tinylog.Level.TRACE.ordinal) {
            return
        }
        val time = TIME_FORMAT.format(logEntry.getTimestamp().toDate())
        val packagePath: Array<String> = logEntry.getClassName().split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val clazz = packagePath[packagePath.size - 1]
        val message: String = ConvertColor.parseColor(logEntry.getMessage())
        val hasException = logEntry.exception != null && logEntry.level == Level.ERROR
        var ansi: Ansi = Ansi.ansi()
        ansi = ansi.fgCyan().a("[$time] ").reset()
        ansi = ansi.a("[" + logEntry.getThread().getName() + "] ").reset()
        ansi = ansi.fgYellow().a("[" + if(hasException) "SEVERE" + "] " else logEntry.getLevel().name + "] ").reset()
        ansi = ansi.fgMagenta().a("[$clazz] ").reset()
        when (logEntry.getLevel()) {
            org.tinylog.Level.INFO, org.tinylog.Level.OFF -> ansi = ansi.a(message).reset()
            org.tinylog.Level.WARN -> ansi = ansi.fgBrightYellow().a(message).reset()
            org.tinylog.Level.ERROR -> ansi = if(hasException) ansi.bgRgb(125, 0, 0).fgDefault().bold().a(" An Exception Occurred: ").reset() else ansi.fgRed().a(message).reset()
            org.tinylog.Level.DEBUG -> ansi = ansi.fgBrightCyan().a(message).reset()
            org.tinylog.Level.TRACE -> ansi = ansi.fgBrightBlue().a(message).reset()
        }
        if(hasException) {
            ansi = ansi.fgBrightBlack().a("\n${logEntry.exception.stackTraceToString()}")
        }
        System.out.println(ansi)
    }

    override fun flush() {
        System.out.flush()
    }

    override fun close() {
    }

    companion object {
        private val TIME_FORMAT = SimpleDateFormat("HH:mm:ss")
    }
}