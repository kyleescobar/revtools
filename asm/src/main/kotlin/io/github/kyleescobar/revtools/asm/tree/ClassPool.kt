package io.github.kyleescobar.revtools.asm.tree

import io.github.kyleescobar.revtools.asm.attribute.OrigInfoAttribute
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class ClassPool {

    private val classSet = mutableSetOf<ClassNode>()
    val resources = mutableMapOf<String, ByteArray>()

    val classes get() = classSet.filter { !it.ignored }.toSet()
    val ignoredClasses get() = classSet.filter { it.ignored }.toSet()
    val allClasses get() = classSet.toSet()

    fun addClass(node: ClassNode) {
        if(classSet.map { it.name }.contains(node.name)) {
            throw IllegalArgumentException("Class with name: ${node.name} already in pool.")
        }
        node.init(this)
        classSet.add(node)
    }

    fun addClass(data: ByteArray) {
        addClass(ClassNode().fromByteArray(data))
    }

    fun removeClass(node: ClassNode) {
        classSet.remove(node)
    }

    fun removeClass(name: String) {
        val node = classSet.first { it.name == name }
        classSet.remove(node)
    }

    fun findClass(name: String) = classSet.filter { !it.ignored }.firstOrNull { it.name == name }
    fun findIgnoredClass(name: String) = classSet.filter { it.ignored }.firstOrNull { it.name == name }
    fun resolveClass(name: String) = findClass(name) ?: findIgnoredClass(name)

    fun resolveMethod(owner: String, name: String, desc: String, isInterface: Boolean = false): MethodNode? {
        var cls: ClassNode? = resolveClass(owner) ?: return null
        var method: MethodNode? = null
        while(cls != null) {
            method = cls.findMethod(name, desc)
            if(method != null) return method.virtualMethods.last()
            cls = cls.superClass
        }
        return null
    }

    fun toJar(file: File, skipIgnored: Boolean = false) {
        if(file.exists()) {
            file.deleteRecursively()
        }
        val classesToWrite = if(skipIgnored) classes else allClasses
        JarOutputStream(FileOutputStream(file)).use { jos ->
            classesToWrite.forEach { cls ->
                jos.putNextEntry(JarEntry(cls.name+".class"))
                jos.write(cls.toByteArray())
                jos.closeEntry()
            }
            resources.forEach { (name, data) ->
                jos.putNextEntry(JarEntry(name))
                jos.write(data)
                jos.closeEntry()
            }
        }
    }

    fun init() {
        allClasses.forEach { cls ->
            cls.superClass = resolveClass(cls.superName)
            cls.superClass?.children?.add(cls)
            cls.interfaces.mapNotNull { resolveClass(it) }.forEach { itf ->
                cls.interfaceClasses.add(itf)
                itf.children.add(cls)
            }
        }
    }

    companion object {

        internal val REGISTERED_ATTRS = arrayOf(OrigInfoAttribute())

        private val IGNORED_PACKAGES = listOf(
            "org"
        )

        fun fromJar(file: File): ClassPool {
            val pool = ClassPool()
            JarFile(file).use { jar ->
                jar.entries().asSequence().forEach { entry ->
                    val name = entry.name
                    val data = jar.getInputStream(entry).readAllBytes()
                    if(!entry.name.endsWith(".class")) {
                        // Skip the META-INF files (JAGEXLTD.RSA, JAGEXLTD.SF, and MANIFEST.MF)
                        if(entry.name.startsWith("META-INF/")) return@forEach
                        pool.resources[name] = data
                    } else {
                        pool.addClass(data)
                    }
                }
            }
            pool.allClasses.forEach { cls ->
                if(IGNORED_PACKAGES.any { cls.name.startsWith(it) }) {
                    cls.ignored = true
                }
            }
            return pool
        }
    }
}