package io.github.kyleescobar.revtools.asm.tree

import io.github.kyleescobar.revtools.asm.attribute.OrigInfoAttribute
import io.github.kyleescobar.revtools.asm.util.field
import io.github.kyleescobar.revtools.asm.util.nullField
import org.objectweb.asm.Attribute
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.lang.reflect.Modifier

internal fun ClassNode.init(pool: ClassPool) {
    this.pool = pool
    methods.forEach { it.init(this) }
    fields.forEach { it.init(this) }

    /*
     * Read attributes.
     */
    if(attrs == null) {
        attrs = mutableListOf<Attribute>(origInfoAttr)
    } else {
        attrs.forEach { attr ->
            when(attr.type) {
                "OrigInfo" -> { origInfoAttr = attr as OrigInfoAttribute }
            }
        }
    }
}

var ClassNode.pool: ClassPool by field()
var ClassNode.ignored: Boolean by field { false }

var ClassNode.superClass: ClassNode? by nullField()
val ClassNode.interfaceClasses: HashSet<ClassNode> by field { hashSetOf() }
val ClassNode.children: HashSet<ClassNode> by field { hashSetOf() }

var ClassNode.origInfoAttr: OrigInfoAttribute by field { OrigInfoAttribute(null, it.name, null) }

val ClassNode.identifier get() = name
val ClassNode.type get() = Type.getObjectType(name)

fun ClassNode.isAbstract() = Modifier.isAbstract(access)
fun ClassNode.isInterface() = Modifier.isInterface(access)

fun ClassNode.findMethod(name: String, desc: String) = methods.firstOrNull { it.name == name && it.desc == desc }
fun ClassNode.findField(name: String, desc: String) = fields.firstOrNull { it.name == name && it.desc == desc }

val ClassNode.superClassAndInterfaceClasses: List<ClassNode> get() {
    val cls = superClass
    return if(cls != null) {
        listOf(cls).plus(interfaceClasses)
    } else {
        interfaceClasses.toList()
    }
}

fun ClassNode.isOverride(name: String, desc: String): Boolean {
    val superClass = this.superClass
    if(superClass != null) {
        if(superClass.findMethod(name, desc) != null) {
            return true
        }
        if(superClass.isOverride(name, desc)) {
            return true
        }
    }
    for(superInterfaceClass in interfaceClasses) {
        if(superInterfaceClass.findMethod(name, desc) != null) {
            return true
        }
        if(superInterfaceClass.isOverride(name, desc)) {
            return true
        }
    }
    return false
}

fun ClassNode.isAssignableFrom(other: ClassNode): Boolean {
    return this == other || this.isSuperClassOf(other) || this.isSuperInterfaceOf(other)
}

fun ClassNode.toByteArray(): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    this.accept(writer)
    return writer.toByteArray()
}

fun ClassNode.fromByteArray(bytes: ByteArray): ClassNode {
    val reader = ClassReader(bytes)
    reader.accept(this, ClassPool.REGISTERED_ATTRS, 0)
    return this
}

private tailrec fun ClassNode.isSuperClassOf(other: ClassNode): Boolean {
    val superClass = other.superClass ?: return false
    if(superClass == this) {
        return true
    }
    return this.isSuperClassOf(superClass)
}

private fun ClassNode.isSuperInterfaceOf(other: ClassNode): Boolean {
    for(superInterface in other.interfaceClasses) {
        if(superInterface == this || this.isSuperInterfaceOf(superInterface)) {
            return true
        }
    }
    return false
}