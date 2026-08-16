package com.aurudu.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aurudu.app.R
import com.aurudu.app.data.Language

object AppFonts {
    val Disapamok = FontFamily(Font(R.font.un_disapamok, FontWeight.Normal))
    val Indeewaree = FontFamily(Font(R.font.un_indeewaree, FontWeight.Normal))
    val Arundathee = FontFamily(Font(R.font.un_arundathee, FontWeight.Normal))
    val Gurulugomi = FontFamily(Font(R.font.un_gurulugomi, FontWeight.Normal))
    val Ganganee = FontFamily(Font(R.font.un_ganganee, FontWeight.Normal))

    // The bundled fonts above only carry Sinhala glyphs, so Tamil text falls
    // back to the system default font (which resolves to a Tamil-capable face).
    fun forLanguage(language: Language, sinhalaFont: FontFamily): FontFamily =
        if (language == Language.TAMIL) FontFamily.Default else sinhalaFont
}

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = AppFonts.Gurulugomi,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)