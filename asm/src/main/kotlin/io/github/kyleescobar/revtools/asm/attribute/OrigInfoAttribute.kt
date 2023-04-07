package io.github.kyleescobar.revtools.asm.attribute

import org.objectweb.asm.*

class OrigInfoAttribute(
    var origOwner: String? = null,
    var origName: String? = null,
    var origDesc: String? = null,
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

        return OrigInfoAttribute(origOwner, origName, origDesc)
    }

    override fun write(
        classWriter: ClassWriter,
        code: ByteArray?,
        codeLength: Int,
        maxStack: Int,
        maxLocals: Int
    ): ByteVector {
        val buf = ByteVector()
        buf.putShort(classWriter.newUTF8(origOwner))
        buf.putShort(classWriter.newUTF8(origName))
        buf.putShort(classWriter.newUTF8(origDesc))
        return buf
    }
}