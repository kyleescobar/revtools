package io.github.kyleescobar.revtools.asm.tree

import io.github.kyleescobar.revtools.asm.util.field
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.lang.reflect.Modifier

internal fun FieldNode.init(owner: ClassNode) {
    this.owner = owner
}

var FieldNode.owner: ClassNode by field()
val FieldNode.pool: ClassPool get() = owner.pool

val FieldNode.identifier get() = "${owner.identifier}.$name"
val FieldNode.type get() = Type.getType(desc)

fun FieldNode.isStatic() = Modifier.isStatic(access)
fun FieldNode.isPrivate() = Modifier.isPrivate(access)

val FieldNode.virtualFields: List<FieldNode> get() {
    val ret = mutableListOf<FieldNode>()
    if(isStatic()) {
        ret.add(this)
        return ret
    }
    findBaseFields(mutableListOf(), owner, name, desc).forEach {
        findInheritedFields(ret, mutableSetOf(), it.owner, it.name, it.desc)
    }
    return ret
}

private fun findBaseFields(fields: MutableList<FieldNode>, cls: ClassNode?, name: String, desc: String): MutableList<FieldNode> {
    if(cls == null) {
        return fields
    }

    val f = cls.findField(name, desc)
    if(f != null && !f.isStatic()) {
        fields.add(f)
    }

    val parentFields = findBaseFields(mutableListOf(), cls.superClass, name, desc)
    cls.interfaceClasses.forEach { parentFields.addAll(findBaseFields(mutableListOf(), it, name, desc)) }

    return if(parentFields.isEmpty()) fields else parentFields
}

private fun findInheritedFields(
    fields: MutableList<FieldNode>,
    visited: MutableSet<ClassNode>,
    cls: ClassNode?,
    name: String,
    desc: String
) {
    if(cls == null || visited.contains(cls)) {
        return
    }
    visited.add(cls)

    val f = cls.findField(name, desc)
    if(f != null && !f.isStatic()) {
        fields.add(f)
    }

    mutableListOf<ClassNode>().also {
        it.addAll(cls.children)
    }.forEach {
        findInheritedFields(fields, visited, it, name, desc)
    }
}