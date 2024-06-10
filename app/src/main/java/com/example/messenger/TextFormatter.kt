package com.example.messenger

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.text.style.StrikethroughSpan


//Формат
object TextFormatter {

    fun formatText(input: String): SpannableString {
        val spannableString = SpannableString(removeSpecialSymbols(input))

        // Применение жирного текста для текста, заключённого в звёздочки (*)
        applySpan(spannableString, input, "\\*(.*?)\\*", StyleSpan(Typeface.BOLD))

        // Применение зачёркнутого текста для текста, заключённого в символы доллара ($)
        applySpan(spannableString, input, "\\$(.*?)\\$", StrikethroughSpan())
        //% кривой
        applySpan(spannableString,input,"\\%(.*?)\\%", StyleSpan(Typeface.ITALIC))


        return spannableString
    }

    private fun removeSpecialSymbols(input: String): String {
        return input.replace(Regex("[*\\$\\%]"), "")
    }

    private fun applySpan(spannableString: SpannableString, originalText: String, regex: String, span: Any) {
        val pattern = Regex(regex)
        val matcher = pattern.findAll(originalText)

        var offset = 0
        for (match in matcher) {
            val start = match.range.first - offset
            val end = match.range.last - offset

            // Применение стиля к тексту, исключая специальные символы
            spannableString.setSpan(span, start, end - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Увеличиваем offset на количество удаленных символов
            offset += 2
        }
    }
}
