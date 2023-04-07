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
        ansi = ansi.fgRgb(150, 150, 140).a("[" + logEntry.getThread().getName() + "] ").reset()
        when (logEntry.getLevel()) {
            Level.TRACE -> { ansi = ansi.fgBrightCyan().a("[TRACE] ").reset() }
            Level.DEBUG -> { ansi = ansi.fgBrightBlue().a("[DEBUG] ").reset() }
            Level.INFO -> { ansi = ansi.fgRgb(122, 216, 122).a("[INFO] ").reset() }
            Level.WARN -> { ansi = ansi.fgBrightYellow().a("[WARN] ").reset() }
            Level.ERROR -> {
                if(hasException) {
                    ansi = ansi.bgRgb(125, 50, 50).fgDefault().a("[SEVERE]").reset().fgRed().a(" ")
                } else {
                    ansi = ansi.fgRed().a("[ERROR] ").reset()
                }
            }
            else -> return
        }
        ansi = ansi.fgMagenta().a("[$clazz] ").reset()
        ansi = ansi.fgDefault().a(message).reset()
        if(hasException) {
            ansi = ansi.reset().fgBrightBlack().a("\n${logEntry.exception.stackTraceToString()}")
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