package io.github.kyleescobar.revtools.asm.tree

import io.github.kyleescobar.revtools.asm.util.field
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Modifier

internal fun MethodNode.init(owner: ClassNode) {
    this.owner = owner
}

var MethodNode.owner: ClassNode by field()
val MethodNode.pool: ClassPool get() = owner.pool

val MethodNode.identifier get() = "${owner.identifier}.$name$desc"
val MethodNode.type get() = Type.getMethodType(desc)

fun MethodNode.isStatic() = Modifier.isStatic(access)
fun MethodNode.isAbstract() = Modifier.isAbstract(access)

fun MethodNode.isConstructor() = name == "<init>"
fun MethodNode.isInitializer() = name == "<clinit>"

val MethodNode.virtualMethods: List<MethodNode> get() {
    val ret = mutableListOf<MethodNode>()
    if(isStatic()) {
        ret.add(this)
        return ret
    }
    findBaseMethods(mutableListOf(), owner, name, desc).forEach {
        findInheritedMethods(ret, mutableSetOf(), it.owner, it.name, it.desc)
    }
    return ret
}

private fun findBaseMethods(methods: MutableList<MethodNode>, cls: ClassNode?, name: String, desc: String): MutableList<MethodNode> {
    if(cls == null) {
        return methods
    }

    val m = cls.findMethod(name, desc)
    if(m != null && !m.isStatic()) {
        methods.add(m)
    }

    val parentMethods = findBaseMethods(mutableListOf(), cls.superClass, name, desc)
    cls.interfaceClasses.forEach { parentMethods.addAll(findBaseMethods(mutableListOf(), it, name, desc)) }

    return if(parentMethods.isEmpty()) methods else parentMethods
}

private fun findInheritedMethods(
    methods: MutableList<MethodNode>,
    visited: MutableSet<ClassNode>,
    cls: ClassNode?,
    name: String,
    desc: String
) {
    if(cls == null || visited.contains(cls)) {
        return
    }
    visited.add(cls)

    val m = cls.findMethod(name, desc)
    if(m != null && !m.isStatic()) {
        methods.add(m)
    }

    mutableListOf<ClassNode>().also {
        it.addAll(cls.children)
        it.addAll(cls.interfaceClasses)
    }.forEach {
        findInheritedMethods(methods, visited, it, name, desc)
    }
}

internal fun MethodNode.build() {

}