package io.github.kyleescobar.revtools.asm.attribute

import org.objectweb.asm.*

class OrigInfoAttribute(
    var owner: String = "",
    var name: String = "",
    var desc: String = "",
    var lineNumbers: MutableList<Int> = mutableListOf()
) : Attribute("OrigInfo") {

    override fun read(
        classReader: ClassReader,
        offset: Int,
        length: Int,
        charBuffer: CharArray,
        codeAttributeOffset: Int,
        labels: Array<out Label>?
    ): OrigInfoAttribute {
        var curOffset = offset

        val origOwner = classReader.readUTF8(curOffset, charBuffer)
        curOffset += 2

        val origName = classReader.readUTF8(curOffset, charBuffer)
        curOffset += 2

        val origDesc = classReader.readUTF8(curOffset, charBuffer)
        curOffset += 2

        val lineNumberCount = classReader.readUnsignedShort(curOffset)
        curOffset += 2

        val lineNumbers = mutableListOf<Int>()
        for(i in 0 until lineNumberCount) {
            val lineNumber = classReader.readInt(curOffset)
            curOffset += 4
            lineNumbers.add(lineNumber)
        }

        return OrigInfoAttribute(origOwner, origName, origDesc, lineNumbers)
    }

    override fun write(
        classWriter: ClassWriter,
        code: ByteArray?,
        codeLength: Int,
        maxStack: Int,
        maxLocals: Int
    ): ByteVector {
        val buf = ByteVector()
        buf.putShort(classWriter.newUTF8(owner))
        buf.putShort(classWriter.newUTF8(name))
        buf.putShort(classWriter.newUTF8(desc))
        buf.putShort(lineNumbers.size)
        lineNumbers.forEach { lineNumber ->
            buf.putInt(lineNumber)
        }
        return buf
    }
}