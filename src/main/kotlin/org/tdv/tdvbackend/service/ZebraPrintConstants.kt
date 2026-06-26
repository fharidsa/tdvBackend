package org.tdv.tdvbackend.service

import kotlin.math.roundToInt

object ZebraPrintConstants {
    const val DEFAULT_DPI = 300

    fun resolveDpi(dpi: Int?): Int {
        val value = dpi ?: DEFAULT_DPI
        require(value == 203 || value == 300) { "DPI de impresora no válido: $value (use 203 o 300)" }
        return value
    }

    fun cmToDots(cm: Double, dpi: Int): Int = (cm * dpi / 2.54).roundToInt()

    /** Escala valores del diseño de etiqueta usuario/fecha calibrado a 300 dpi. */
    fun scaleFrom300(dotsAt300: Int, dpi: Int): Int = (dotsAt300 * dpi / 300.0).roundToInt()

    /** Escala valores del diseño de etiqueta de conteo calibrado a 203 dpi. */
    fun scaleFrom203(dotsAt203: Int, dpi: Int): Int = (dotsAt203 * dpi / 203.0).roundToInt()
}
