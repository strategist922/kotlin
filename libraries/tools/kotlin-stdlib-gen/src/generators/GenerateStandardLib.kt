package generators

import java.io.*
import templates.*
import templates.Family.*

private val COMMON_AUTOGENERATED_WARNING: String = """//
// NOTE THIS FILE IS AUTO-GENERATED by the GenerateStandardLib.kt
// See: https://github.com/JetBrains/kotlin/tree/master/libraries/stdlib
//"""

fun generateFile(outFile: File, header: String, inputFile: File, f: (String)-> String) {
    generateFile(outFile, header, arrayListOf(inputFile), f)
}

fun generateFile(outFile: File, header: String, inputFile: File, jvmFile: File, f: (String)-> String) {
    generateFile(outFile, header, arrayListOf(inputFile, jvmFile), f)
}

fun generateFile(outFile: File, header: String, inputFiles: List<File>, f: (String)-> String) {
    outFile.getParentFile()?.mkdirs()
    val writer = PrintWriter(FileWriter(outFile))
    try {
        writer.println(header)

        for (file in inputFiles) {
            writer.println("""
$COMMON_AUTOGENERATED_WARNING
// Generated from input file: $file
//
""")

        println("Parsing $file and writing $outFile")
        val reader = FileReader(file).buffered()
        try {
            // TODO ideally we'd use a filterNot() here :)
            val iter = reader.lineIterator()
            while (iter.hasNext()) {
                val line = iter.next()

                if (line.startsWith("package")) continue

                val xform = f(line)
                writer.println(xform)
            }
        } finally {
            reader.close()
            reader.close()
        }
        }
    } finally {
        writer.close()
    }
}


/**
 * Generates methods in the standard library which are mostly identical
 * but just using a different input kind.
 *
 * Kinda like mimicking source macros here, but this avoids the inefficiency of type conversions
 * at runtime.
 */
fun main(args: Array<String>) {
    require(args.size == 1, "Expecting Kotlin project home path as an argument")

    val outDir = File(File(args[0]), "libraries/stdlib/src/generated")
    require(outDir.exists(), "${outDir.getPath()} doesn't exist!")

    val jsCoreDir = File(args[0], "js/js.libraries/src/core")
    require(jsCoreDir.exists(), "${jsCoreDir.getPath()} doesn't exist!")

    generateDomAPI(File(jsCoreDir, "dom.kt"))
    generateDomEventsAPI(File(jsCoreDir, "domEvents.kt"))

    val otherArrayNames = arrayListOf("Boolean", "Byte", "Char", "Short", "Int", "Long", "Float", "Double")

    iterators()
    templates.writeTo(File(outDir, "Iterators.kt")) {
        buildFor(Iterators, "")
    }

    val iteratorSignatures = templates.map { it.signature.flat() }.toSet()
    templates.clear()

    collections()
    templates.writeTo(File(outDir, "_Arrays.kt")) {
        buildFor(Arrays, "")
    }

    for (a in otherArrayNames) {
        templates.writeTo(File(outDir, "_${a}Arrays.kt")) {
            buildFor(PrimitiveArrays, a)
        }
    }

    templates.writeTo(File(outDir, "_Iterables.kt")) {
        if (iteratorSignatures contains signature.flat()) "" else buildFor(Iterables, "")
    }

    templates.writeTo(File(outDir, "_IteratorsCommon.kt")) {
        if (iteratorSignatures contains signature.flat()) "" else buildFor(Iterators, "")
    }

    templates.writeTo(File(outDir, "_Collections.kt")) {
        if (iteratorSignatures contains signature.flat()) buildFor(Collections, "") else ""
    }

    generateDownTos(File(outDir, "_DownTo.kt"), "package kotlin")
}

fun String.flat() = this.replaceAll(" ", "")

fun List<GenericFunction>.writeTo(file : File, builder : GenericFunction.() -> String) {
    println("Generating file: ${file.getPath()}")
    val its = FileWriter(file)

    its.use {
        its.append("package kotlin\n\n")
        its.append("import java.util.*\n\n")
        for (t in this) {
            its.append(t.builder())
        }
    }
}

// Pretty hacky way to code generate; ideally we'd be using the AST and just changing the function prototypes
fun replaceGenerics(arrayName: String, it: String): String {
    return it.replaceAll(" <in T>", " ").replaceAll("<in T, ", "<").replaceAll("<T, ", "<").replaceAll("<T,", "<").
    replaceAll(" <T> ", " ").
    replaceAll("<T>", "<${arrayName}>").replaceAll("<in T>", "<${arrayName}>").
    replaceAll("\\(T\\)", "(${arrayName})").replaceAll("T\\?", "${arrayName}?").
    replaceAll("T,", "${arrayName},").
    replaceAll("T\\)", "${arrayName})").
    replaceAll(" T ", " ${arrayName} ")
}

