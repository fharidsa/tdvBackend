package org.tdv.tdvbackend.service

import org.tdv.tdvbackend.service.ZebraPrintConstants.scaleFrom203

/** Plantilla completa de conteo (~4 x 3.15 pulgadas; 203 o 300 dpi). */
object ConteoLabelZplBuilder {

    private fun d(valueAt203: Int, dpi: Int): Int = scaleFrom203(valueAt203, dpi)

    fun build(
        usuario: String,
        codigoIca: String,
        fecha: String,
        hora: String,
        cantidadEsperada: Int,
        conteoRealizado: Int,
        dpi: Int = ZebraPrintConstants.DEFAULT_DPI,
    ): String {
        val resolvedDpi = ZebraPrintConstants.resolveDpi(dpi)
        val safeUsuario = UserDateLabelZplBuilder.sanitizeForZpl(usuario)
        val safeCodigoIca = UserDateLabelZplBuilder.sanitizeForZpl(codigoIca)
        val safeFecha = UserDateLabelZplBuilder.sanitizeForZpl(fecha)
        val safeHora = UserDateLabelZplBuilder.sanitizeForZpl(hora)

        return buildString {
            append("^XA\n")
            append("^MNW\n")
            append("^LH0,0\n")
            append("^PW${d(800, resolvedDpi)}\n")
            append("^LL${d(640, resolvedDpi)}\n")

            append("^FO${d(20, resolvedDpi)},${d(10, resolvedDpi)}^GB${d(760, resolvedDpi)},${d(380, resolvedDpi)},${d(3, resolvedDpi)}^FS\n")

            append("^A0N,${d(28, resolvedDpi)},${d(28, resolvedDpi)}\n")
            append("^FO${d(40, resolvedDpi)},${d(22, resolvedDpi)}^FDUSUARIO:^FS\n")
            append("^A0N,${d(32, resolvedDpi)},${d(32, resolvedDpi)}\n")
            append("^FO${d(180, resolvedDpi)},${d(20, resolvedDpi)}^FD$safeUsuario^FS\n")

            append("^FO${d(20, resolvedDpi)},${d(58, resolvedDpi)}^GB${d(760, resolvedDpi)},${d(2, resolvedDpi)},${d(2, resolvedDpi)}^FS\n")

            append("^A0N,${d(45, resolvedDpi)},${d(45, resolvedDpi)}\n")
            append("^FO${d(160, resolvedDpi)},${d(75, resolvedDpi)}^FD$safeCodigoIca^FS\n")

            append("^FO${d(20, resolvedDpi)},${d(148, resolvedDpi)}^GB${d(760, resolvedDpi)},${d(4, resolvedDpi)},${d(4, resolvedDpi)}^FS\n")

            append("^A0N,${d(32, resolvedDpi)},${d(32, resolvedDpi)}\n")
            append("^FO${d(40, resolvedDpi)},${d(162, resolvedDpi)}^FDFECHA^FS\n")
            append("^A0N,${d(32, resolvedDpi)},${d(32, resolvedDpi)}\n")
            append("^FO${d(540, resolvedDpi)},${d(162, resolvedDpi)}^FDHORA^FS\n")
            append("^A0N,${d(44, resolvedDpi)},${d(44, resolvedDpi)}\n")
            append("^FO${d(40, resolvedDpi)},${d(197, resolvedDpi)}^FD$safeFecha^FS\n")
            append("^A0N,${d(44, resolvedDpi)},${d(44, resolvedDpi)}\n")
            append("^FO${d(540, resolvedDpi)},${d(197, resolvedDpi)}^FD$safeHora^FS\n")

            append("^FO${d(20, resolvedDpi)},${d(250, resolvedDpi)}^GB${d(760, resolvedDpi)},${d(2, resolvedDpi)},${d(2, resolvedDpi)}^FS\n")

            append("^A0N,${d(28, resolvedDpi)},${d(28, resolvedDpi)}\n")
            append("^FO${d(60, resolvedDpi)},${d(265, resolvedDpi)}^FDCANTIDAD ESPERADA^FS\n")
            append("^A0N,${d(28, resolvedDpi)},${d(28, resolvedDpi)}\n")
            append("^FO${d(460, resolvedDpi)},${d(265, resolvedDpi)}^FDCONTEO REALIZADO^FS\n")

            append("^A0N,${d(80, resolvedDpi)},${d(80, resolvedDpi)}\n")
            append("^FO${d(130, resolvedDpi)},${d(305, resolvedDpi)}^FD$cantidadEsperada^FS\n")
            append("^A0N,${d(80, resolvedDpi)},${d(80, resolvedDpi)}\n")
            append("^FO${d(540, resolvedDpi)},${d(305, resolvedDpi)}^FD$conteoRealizado^FS\n")

            append("^FO${d(400, resolvedDpi)},${d(255, resolvedDpi)}^GB${d(3, resolvedDpi)},${d(135, resolvedDpi)},${d(3, resolvedDpi)}^FS\n")

            append("^XZ\n")
        }
    }
}
