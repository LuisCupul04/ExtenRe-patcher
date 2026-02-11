package com.extenre.patcher.util.proxy.mutableTypes

import com.extenre.patcher.util.proxy.mutableTypes.MutableAnnotation.Companion.toMutable
import com.extenre.patcher.util.proxy.mutableTypes.MutableField.Companion.toMutable
import com.extenre.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import com.android.tools.smali.dexlib2.base.reference.BaseTypeReference
import com.android.tools.smali.dexlib2.iface.ClassDef
import com.android.tools.smali.dexlib2.util.FieldUtil
import com.android.tools.smali.dexlib2.util.MethodUtil

class MutableClass(classDef: ClassDef) : ClassDef, BaseTypeReference() {
    // Class
    private var type = classDef.type
    private var sourceFile = classDef.sourceFile
    private var accessFlags = classDef.accessFlags
    private var superclass = classDef.superclass

    private val _interfaces: MutableList<String> by lazy {
        classDef.interfaces.toMutableList()
    }

    private val _annotations: MutableSet<MutableAnnotation> by lazy {
        classDef.annotations.map { it.toMutable() }.toMutableSet()
    }

    // Methods
    private val _methods: MutableSet<MutableMethod> by lazy {
        classDef.methods.map { it.toMutable() }.toMutableSet()
    }

    private val _directMethods: MutableSet<MutableMethod> by lazy {
        _methods.filter { MethodUtil.METHOD_IS_DIRECT.test(it) }.toMutableSet()
    }

    private val _virtualMethods: MutableSet<MutableMethod> by lazy {
        _methods.filter { MethodUtil.METHOD_IS_VIRTUAL.test(it) }.toMutableSet()
    }

    // Fields
    private val _fields: MutableSet<MutableField> by lazy {
        classDef.fields.map { it.toMutable() }.toMutableSet()
    }

    private val _staticFields: MutableSet<MutableField> by lazy {
        _fields.filter { FieldUtil.FIELD_IS_STATIC.test(it) }.toMutableSet()
    }

    private val _instanceFields: MutableSet<MutableField> by lazy {
        _fields.filter { FieldUtil.FIELD_IS_INSTANCE.test(it) }.toMutableSet()
    }

    fun setType(type: String) {
        this.type = type
    }

    fun setSourceFile(sourceFile: String?) {
        this.sourceFile = sourceFile
    }

    fun setAccessFlags(accessFlags: Int) {
        this.accessFlags = accessFlags
    }

    fun setSuperClass(superclass: String?) {
        this.superclass = superclass
    }

    override fun getType(): String = type
    override fun getAccessFlags(): Int = accessFlags
    override fun getSourceFile(): String? = sourceFile
    override fun getSuperclass(): String? = superclass

    override fun getInterfaces(): MutableList<String> = _interfaces
    override fun getAnnotations(): MutableSet<MutableAnnotation> = _annotations

    override fun getStaticFields(): MutableSet<MutableField> = _staticFields
    override fun getInstanceFields(): MutableSet<MutableField> = _instanceFields
    override fun getFields(): MutableSet<MutableField> = _fields

    override fun getDirectMethods(): MutableSet<MutableMethod> = _directMethods
    override fun getVirtualMethods(): MutableSet<MutableMethod> = _virtualMethods
    override fun getMethods(): MutableSet<MutableMethod> = _methods

    companion object {
        fun ClassDef.toMutable(): MutableClass = MutableClass(this)
    }
}