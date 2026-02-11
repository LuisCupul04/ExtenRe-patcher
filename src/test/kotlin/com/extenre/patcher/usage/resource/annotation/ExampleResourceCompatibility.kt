package com.extenre.patcher.usage.resource.annotation

import com.extenre.patcher.annotation.Compatibility
import com.extenre.patcher.annotation.Package

@Compatibility(
    [Package(
        "com.example.examplePackage", arrayOf("0.0.1", "0.0.2")
    )]
)
@Target(AnnotationTarget.CLASS)
internal annotation class ExampleResourceCompatibility

