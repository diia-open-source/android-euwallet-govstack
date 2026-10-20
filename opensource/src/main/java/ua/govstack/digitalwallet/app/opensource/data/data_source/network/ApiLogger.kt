package ua.govstack.digitalwallet.app.opensource.data.data_source.network

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.Reusable
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import javax.inject.Inject

@Reusable
class ApiLogger @Inject constructor() : HttpLoggingInterceptor.Logger {

    override fun log(message: String) {
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val msg = JsonParser.parseString(message)
                loopThroughJson(msg)
                val prettyPrintJson = GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .create()
                    .toJson(msg)
                Platform.get().log(prettyPrintJson, Platform.INFO, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Platform.get().log(message, Platform.INFO, null)
            return
        }
    }

    @Throws(JSONException::class)
    fun loopThroughJson(input: Any) {
        if (input is JsonObject) {
            val keys: Set<String> = input.keySet()
            val base64 = mutableListOf<String>()
            keys.forEach { key ->
                if (input[key] !is JsonArray) {
                    if (input[key] is JsonObject) {
                        loopThroughJson(input[key])
                    } else {
                        if (key == "photo" || key == "sign") {
                            base64.add(key)
                        }
                    }
                } else {
                    loopThroughJson(input[key])
                }
            }
            base64.forEach {
                input.remove(it)
                input.addProperty(it, "")
            }
        }
        if (input is JsonArray) {
            input.forEach {
                loopThroughJson(it)
            }
        }
    }
}