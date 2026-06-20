package org.tdv.tdvbackend.service

/** Genera ZPL para etiqueta 1 x 3cm (usuario + fecha) a 203 dpi. */
object UserDateLabelZplBuilder {

    private const val DOTS_PER_INCH = 203
    // Etiqueta física: 1 pulgada ancho × 3 cm alto = 203 × 240 dots
    private const val LABEL_WIDTH_DOTS = 203
    private const val LABEL_HEIGHT_DOTS = 240  // 3cm × (203/2.54) ≈ 240
    // Offset vertical para centrar el contenido (0.8cm de margen superior = 64 dots)
    private const val LABEL_VERTICAL_OFFSET = 64

    fun build(usuario: String, fecha: String): String {
        val safeUsuario = sanitizeForZpl(usuario)
        val safeFecha = sanitizeForZpl(fecha)

        val labelWidth = LABEL_WIDTH_DOTS
        val labelHeight = LABEL_HEIGHT_DOTS
        val margin = 8
        val innerWidth = labelWidth - (margin * 2)
        val innerHeight = labelHeight - (margin * 2)
        val usuarioFont = 38
        val fechaFont = 34
        val usuarioLineSpacing = 8
        val separatorThickness = 2
        val regionPadding = 4

        val separatorY = margin + innerHeight / 2
        val topRegionTop = margin + regionPadding
        val topRegionHeight = separatorY - regionPadding - topRegionTop
        val bottomRegionTop = separatorY + separatorThickness + regionPadding
        val bottomRegionHeight = margin + innerHeight - regionPadding - bottomRegionTop

        val usuarioLines =
            estimateWrappedLines(
                text = safeUsuario,
                fieldWidthDots = innerWidth,
                avgCharWidthDots = (usuarioFont * 0.55).toInt().coerceAtLeast(12),
                maxLines = 2,
            )
        val usuarioBlockHeight = textBlockHeight(usuarioLines, usuarioFont, usuarioLineSpacing)
        val usuarioY = verticallyCenteredY(topRegionTop, topRegionHeight, usuarioBlockHeight)

        val fechaBlockHeight = textBlockHeight(1, fechaFont, 0)
        val fechaY = verticallyCenteredY(bottomRegionTop, bottomRegionHeight, fechaBlockHeight)

        return buildString {
            append("^XA\n")
            append("^MNW\n")
            append("^LH0,$LABEL_VERTICAL_OFFSET\n")
            append("^PW$labelWidth\n")
            append("^LL$labelHeight\n")

            append("^FO$margin,$margin^GB$innerWidth,$innerHeight,2^FS\n")

            append("^A0N,$usuarioFont,$usuarioFont\n")
            append("^FO$margin,$usuarioY^FB$innerWidth,$usuarioLines,$usuarioLineSpacing,C^FD$safeUsuario^FS\n")

            append("^FO$margin,$separatorY^GB$innerWidth,$separatorThickness,$separatorThickness^FS\n")

            append("^A0N,$fechaFont,$fechaFont\n")
            append("^FO$margin,$fechaY^FB$innerWidth,1,0,C^FD$safeFecha^FS\n")

            append("^XZ\n")
        }
    }

    /** Evita caracteres de control ZPL en datos de campo (^FD). */
    fun sanitizeForZpl(text: String): String =
        text
            .replace('\\', ' ')
            .replace('^', ' ')
            .trim()
            .ifBlank { "-" }

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
