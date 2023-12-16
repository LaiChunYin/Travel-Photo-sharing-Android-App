package com.example.travel_photo_sharing_app.utils

import android.content.Context
import android.util.Log
import com.example.travel_photo_sharing_app.R
import java.util.Locale
import java.text.SimpleDateFormat

val tag = "Utils"

fun getCategorySpinnerList(context: Context): MutableList<String>{
    val categoryOptions = context.resources.getStringArray(R.array.category_options);
    Log.d(tag, "options are ${categoryOptions}")
    return categoryOptions.toMutableList()
}

fun formatTimeString(timestampString: String): String {
    val format = "MMMM dd, yyyy"
    val inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS"
    val parsedDateTime = SimpleDateFormat(inputFormat).parse(timestampString)
    Log.d(tag, "parsed time string(${timestampString}) is ${parsedDateTime}")
    return SimpleDateFormat(format, Locale.US).format(parsedDateTime)
}


