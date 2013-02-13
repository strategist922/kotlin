// Auto-generated by org.jetbrains.jet.generators.tests.GenerateRangesCodegenTestData. DO NOT EDIT!
import java.util.ArrayList
import java.lang as j

fun box(): String {
    val list1 = ArrayList<Int>()
    val range1 = 5 downTo 5
    for (i in range1) {
        list1.add(i)
    }
    if (list1 != listOf<Int>(5)) {
        return "Wrong elements for 5 downTo 5: $list1"
    }

    val list2 = ArrayList<Byte>()
    val range2 = 5.toByte() downTo 5.toByte()
    for (i in range2) {
        list2.add(i)
    }
    if (list2 != listOf<Byte>(5.toByte())) {
        return "Wrong elements for 5.toByte() downTo 5.toByte(): $list2"
    }

    val list3 = ArrayList<Short>()
    val range3 = 5.toShort() downTo 5.toShort()
    for (i in range3) {
        list3.add(i)
    }
    if (list3 != listOf<Short>(5.toShort())) {
        return "Wrong elements for 5.toShort() downTo 5.toShort(): $list3"
    }

    val list4 = ArrayList<Long>()
    val range4 = 5.toLong() downTo 5.toLong()
    for (i in range4) {
        list4.add(i)
    }
    if (list4 != listOf<Long>(5.toLong())) {
        return "Wrong elements for 5.toLong() downTo 5.toLong(): $list4"
    }

    val list5 = ArrayList<Char>()
    val range5 = 'k' downTo 'k'
    for (i in range5) {
        list5.add(i)
    }
    if (list5 != listOf<Char>('k')) {
        return "Wrong elements for 'k' downTo 'k': $list5"
    }

    val list6 = ArrayList<Double>()
    val range6 = 5.0 downTo 5.0
    for (i in range6) {
        list6.add(i)
    }
    if (list6 != listOf<Double>(5.0)) {
        return "Wrong elements for 5.0 downTo 5.0: $list6"
    }

    val list7 = ArrayList<Float>()
    val range7 = 5.0.toFloat() downTo 5.0.toFloat()
    for (i in range7) {
        list7.add(i)
    }
    if (list7 != listOf<Float>(5.0.toFloat())) {
        return "Wrong elements for 5.0.toFloat() downTo 5.0.toFloat(): $list7"
    }

    return "OK"
}
