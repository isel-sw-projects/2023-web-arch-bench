package pt.isel

import java.math.BigDecimal
import java.math.RoundingMode
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.pow

object RandomHelper {

    fun randomInt(min: Int, max: Int): Int {
        var max = max
        if (max < Int.MAX_VALUE) max++
        return ThreadLocalRandom.current().nextInt(min, max)
    }

    fun randomLong(min: Long, max: Long): Long {
        var max = max
        if (max < Long.MAX_VALUE) max++
        return ThreadLocalRandom.current().nextLong(min, max)
    }

    fun randomTime(min: Date, max: Date): Date {
        val time = randomLong(min.time, max.time)
        return Date(time)
    }

    fun randomDouble(min: Double, max: Double): Double {
        return ThreadLocalRandom.current().nextDouble(min, max)
    }

    fun randomDouble(max: Double): Double {
        return ThreadLocalRandom.current().nextDouble(max) //bound
    }

    fun randomDouble(): Double {
        return ThreadLocalRandom.current().nextDouble()
    }

    fun randomDecimal(precision: Int, scale: Int): BigDecimal {
        val randD = ThreadLocalRandom.current().nextDouble(0.0, 10.0.pow((precision - scale).toDouble()))
        val decimal = BigDecimal(randD).setScale(scale, RoundingMode.HALF_UP)
        return decimal
    }

    fun randomDecimal(scale: Int, min: Double, max: Double): BigDecimal {
        val randD = randomDouble(min, max)
        val decimal = BigDecimal(randD).setScale(scale, RoundingMode.HALF_UP)
        // return decimal.doubleValue();
        return decimal
    }

    fun randomFloat(): Float {
        return ThreadLocalRandom.current().nextFloat()
    }

    fun randomBoolean(): Boolean {
        return ThreadLocalRandom.current().nextBoolean()
    }

    fun randomString(length: Int, characters: String): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            val index = randomInt(0, characters.length - 1)
            sb.append(characters[index])
        }
        return sb.toString()
    }

    fun randomString(length: Int): String {
        val characters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZ" //Upper Case Letters
                + "abcdefghijklmnopqrstuvxyz" //Lower Case Letters
                + "0123456789")
        //Numbers
        //+ "*@#+%"                     //Special Symbols

        return randomString(length, characters)
    }

    fun randomString(minLen: Int, maxLen: Int): String {
        val length = randomInt(minLen, maxLen)
        return randomString(length)
    }

    fun randomNumberString(length: Int): String {
        val characters = "0123456789"
        return randomString(length, characters)
    }

    fun randomBitMap(setBitCount: Int, mapSize: Int): BitSet {
        require(setBitCount < mapSize) { "setBitCount=$setBitCount must less than mapSize=$mapSize ." }
        val bitmap = BitSet(mapSize)
        while (bitmap.cardinality() < setBitCount) {
            val index = randomInt(0, mapSize + 1)
            bitmap.set(index)
        }
        return bitmap
    }

    fun nuRand(a: Int, x: Int, y: Int): Int {
        val c_255 = randomInt(0, 255)
        val c_1023 = randomInt(0, 1023)
        val c_8191 = randomInt(0, 8191)
        var c = 0
        c = when (a) {
            255 -> c_255
            1023 -> c_1023
            8191 -> c_8191
            else -> throw IllegalArgumentException("NURand: unexpected value ($a) of A used. ")
        }
        return (((randomInt(0, a) or randomInt(x, y)) + c) % (y - x + 1)) + x
    }

    fun randomPermutation(size: Int): IntArray {
        val nums = IntArray(size)
        for (i in 0 until size) {
            nums[i] = i + 1
        }
        for (i in nums.indices) {
            val j = randomInt(i, size - 1)
            val t = nums[i]
            nums[i] = nums[j]
            nums[j] = t
        }
        return nums
    }

    fun lastName(num: Int): String {
        var name: String? = null
        val n = arrayOf("BAR", "OUGHT", "ABLE", "PRI", "PRES", "ESE", "ANTI", "CALLY", "ATION", "EING")
        name = n[num / 100]
        name += n[num / 10 % 10]
        name += n[num % 10]
        return name
    }

    // public static final float newOrderRatio = 0.45f;
    const val paymentRation: Float = 0.43f
    const val orderStatusRation: Float = 0.04f
    const val deliveryRation: Float = 0.04f
    const val stockLevelRation: Float = 0.04f

    fun randomTransaction(
        paymentRation: Float, orderStatusRation: Float, deliveryRation: Float,
        stockLevelRation: Float
    ): TransactionType {
        val chance = randomFloat()
        return if (paymentRation >= chance) {
            TransactionType.Payment
        } else if (paymentRation < chance && chance <= (paymentRation + orderStatusRation)) {
            TransactionType.OrderStatus
        } else if ((paymentRation + orderStatusRation) < chance
            && chance <= (paymentRation + orderStatusRation + deliveryRation)
        ) {
            TransactionType.Delivery
        } else if ((paymentRation + orderStatusRation + deliveryRation) < chance
            && chance <= (paymentRation + orderStatusRation + deliveryRation + stockLevelRation)
        ) {
            TransactionType.StockLevel
        } else {
            TransactionType.NewOrder
        }
    }

    fun randomTransaction(): TransactionType {
        return randomTransaction(paymentRation, orderStatusRation, deliveryRation, stockLevelRation)
    }

    fun getNow(): Timestamp {
        return Timestamp(Date().time)
    }
}
