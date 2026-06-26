package org.tdv.tdvbackend.service

import java.text.Normalizer
import org.tdv.tdvbackend.service.ZebraPrintConstants.cmToDots
import org.tdv.tdvbackend.service.ZebraPrintConstants.resolveDpi
import org.tdv.tdvbackend.service.ZebraPrintConstants.scaleFrom300

/**
 * Etiqueta física 3,2 x 3 cm. Contenido centrado en cuadrado 2 x 2 cm (300 dpi por defecto).
 */
object UserDateLabelZplBuilder {

    private const val LABEL_WIDTH_CM = 3.2
    private const val LABEL_HEIGHT_CM = 3.0
    private const val CONTENT_SIZE_CM = 2.0
    private const val MAX_USUARIO_LENGTH = 26

    fun build(
        usuario: String,
        fecha: String,
        dpi: Int = ZebraPrintConstants.DEFAULT_DPI,
    ): String {
        val resolvedDpi = resolveDpi(dpi)
        val safeUsuario = sanitizeForZpl(usuario.take(MAX_USUARIO_LENGTH))
        val safeFecha = sanitizeForZpl(fecha)

        val labelWidth = cmToDots(LABEL_WIDTH_CM, resolvedDpi)
        val labelHeight = cmToDots(LABEL_HEIGHT_CM, resolvedDpi)
        val contentSize = cmToDots(CONTENT_SIZE_CM, resolvedDpi)
        val contentX = (labelWidth - contentSize) / 2
        val contentY = (labelHeight - contentSize) / 2

        val margin = scaleFrom300(6, resolvedDpi)
        val innerWidth = contentSize - (margin * 2)
        val innerHeight = contentSize - (margin * 2)
        val boxX = contentX + margin
        val boxY = contentY + margin

        val usuarioFont = scaleFrom300(32, resolvedDpi)
        val fechaFont = scaleFrom300(28, resolvedDpi)
        val usuarioLineSpacing = scaleFrom300(4, resolvedDpi)
        val separatorThickness = scaleFrom300(2, resolvedDpi).coerceAtLeast(2)
        val regionPadding = scaleFrom300(3, resolvedDpi)
        val borderThickness = scaleFrom300(2, resolvedDpi).coerceAtLeast(2)

        val separatorY = boxY + innerHeight / 2
        val topRegionTop = boxY + regionPadding
        val topRegionHeight = separatorY - regionPadding - topRegionTop
        val bottomRegionTop = separatorY + separatorThickness + regionPadding
        val bottomRegionHeight = boxY + innerHeight - regionPadding - bottomRegionTop

        val usuarioLines =
            estimateWrappedLines(
                text = safeUsuario,
                fieldWidthDots = innerWidth,
                avgCharWidthDots = (usuarioFont * 0.55).toInt().coerceAtLeast(10),
                maxLines = 2,
            )
        val usuarioBlockHeight = textBlockHeight(usuarioLines, usuarioFont, usuarioLineSpacing)
        val usuarioY = verticallyCenteredY(topRegionTop, topRegionHeight, usuarioBlockHeight)

        val fechaBlockHeight = textBlockHeight(1, fechaFont, 0)
        val fechaY = verticallyCenteredY(bottomRegionTop, bottomRegionHeight, fechaBlockHeight)

        return buildString {
            append("^XA\n")
            append("^MNW\n")
            append("^LH0,0\n")
            append("^PW$labelWidth\n")
            append("^LL$labelHeight\n")

            append("^FO$boxX,$boxY^GB$innerWidth,$innerHeight,$borderThickness^FS\n")

            append("^A0N,$usuarioFont,$usuarioFont\n")
            append("^FO$boxX,$usuarioY^FB$innerWidth,$usuarioLines,$usuarioLineSpacing,C^FD$safeUsuario^FS\n")

            append("^FO$boxX,$separatorY^GB$innerWidth,$separatorThickness,$separatorThickness^FS\n")

            append("^A0N,$fechaFont,$fechaFont\n")
            append("^FO$boxX,$fechaY^FB$innerWidth,1,0,C^FD$safeFecha^FS\n")

            append("^XZ\n")
        }
    }

    /** Normaliza a ASCII y evita caracteres de control ZPL en datos de campo (^FD). */
    fun sanitizeForZpl(text: String): String =
        stripToAscii(text)
            .replace('\\', ' ')
            .replace('^', ' ')
            .trim()
            .ifBlank { "-" }

    private fun stripToAscii(text: String): String {
        val normalized = Normalizer.normalize(text, Normalizer.Form.NFD)
        return normalized.replace(Regex("[^\\p{ASCII}]"), "")
    }

    private fun textBlockHeight(lineCount: Int, fontHeight: Int, lineSpacing: Int): Int =
        if (lineCount <= 0) {
            0
        } else {
            lineCount * fontHeight + (lineCount - 1) * lineSpacing
        }

    private fun verticallyCenteredY(regionTop: Int, regionHeight: Int, blockHeight: Int): Int =
        regionTop + ((regionHeight - blockHeight).coerceAtLeast(0) / 2)

    private fun estimateWrappedLines(
        text: String,
        fieldWidthDots: Int,
        avgCharWidthDots: Int,
        maxLines: Int,
    ): Int {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 1

        val charsPerLine = (fieldWidthDots / avgCharWidthDots).coerceAtLeast(1)
        var lines = 1
        var currentLineChars = 0

        for (word in trimmed.split(Regex("\\s+"))) {
            val wordLen = word.length
            if (wordLen > charsPerLine) {
                if (currentLineChars > 0) {
                    lines++
                    currentLineChars = 0
                }
                var remaining = wordLen
                while (remaining > charsPerLine) {
                    lines++
                    remaining -= charsPerLine
                }
                currentLineChars = remaining
            } else if (currentLineChars == 0) {
                currentLineChars = wordLen
            } else if (currentLineChars + 1 + wordLen <= charsPerLine) {
                currentLineChars += 1 + wordLen
            } else {
                lines++
                currentLineChars = wordLen
            }
            if (lines >= maxLines) return maxLines
        }
        return lines.coerceIn(1, maxLines)
    }
}
