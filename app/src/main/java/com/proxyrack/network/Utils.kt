package com.proxyrack.network

import android.annotation.SuppressLint
import android.os.Build
import com.google.gson.Gson
import com.proxyrack.network.model.step1.SystemInfo
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream
import java.util.*

var uniqueId: String? = null

@SuppressLint("HardwareIds")
fun getDeviceId(): String {
    if (uniqueId == null) {
        uniqueId = UUID.randomUUID().toString()
    }
    return uniqueId!!
}

@SuppressLint("HardwareIds")
fun getSystemInfo(): SystemInfo {
    return SystemInfo(
        sdkVersion = Build.VERSION.RELEASE,
        architecture = System.getProperty("os.arch")!!
    )
}

fun createRetrofitClient(url: String): Retrofit {
    return Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun InputStream.getString(): String {

    val data = ByteArray(2048)
    val length = read(data)

    val result = String(data, 0, length)
    return result.removeSuffix(endOfString())
}

fun InputStream.getBytes(): ByteArray {

    val data = ByteArray(512)
    val length = read(data)

    return data.copyOfRange(0, length)
}

fun endOfString(): String {
    return String(Character.toChars(Integer.parseInt("0000", 16)))
}

inline fun <reified T> String.toObject(): T {
    return Gson().fromJson<T>(this, T::class.java)
}

fun Int.toIP(): String {
    return (this shr 24 and 0xFF).toString() + "." +
            (this shr 16 and 0xFF) + "." +
            (this shr 8 and 0xFF) + "." +
            (this and 0xFF)
}

fun String.asIntArray(): List<Byte> {
    return split(".").map { it.toInt().toByte() }
}