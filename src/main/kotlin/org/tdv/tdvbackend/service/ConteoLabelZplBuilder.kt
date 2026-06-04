package org.tdv.tdvbackend.service

/** Plantilla completa de conteo (~4 x 3.15 pulgadas a 203 dpi). */
object ConteoLabelZplBuilder {

    fun build(
        usuario: String,
        codigoIca: String,
        fecha: String,
        hora: String,
        cantidadEsperada: Int,
        conteoRealizado: Int,
    ): String {
        val safeUsuario = UserDateLabelZplBuilder.sanitizeForZpl(usuario)
        val safeCodigoIca = UserDateLabelZplBuilder.sanitizeForZpl(codigoIca)
        val safeFecha = UserDateLabelZplBuilder.sanitizeForZpl(fecha)
        val safeHora = UserDateLabelZplBuilder.sanitizeForZpl(hora)

        return buildString {
            append("^XA\n")
            append("^MNW\n")
            append("^LH0,0\n")
            append("^PW800\n")
            append("^LL640\n")

            append("^FO20,10^GB760,380,3^FS\n")

            append("^A0N,28,28\n")
            append("^FO40,22^FDUSUARIO:^FS\n")
            append("^A0N,32,32\n")
            append("^FO180,20^FD$safeUsuario^FS\n")

            append("^FO20,58^GB760,2,2^FS\n")

            append("^A0N,45,45\n")
            append("^FO160,75^FD$safeCodigoIca^FS\n")

            append("^FO20,148^GB760,4,4^FS\n")

            append("^A0N,32,32\n")
            append("^FO40,162^FDFECHA^FS\n")
            append("^A0N,32,32\n")
            append("^FO540,162^FDHORA^FS\n")
            append("^A0N,44,44\n")
            append("^FO40,197^FD$safeFecha^FS\n")
            append("^A0N,44,44\n")
            append("^FO540,197^FD$safeHora^FS\n")

            append("^FO20,250^GB760,2,2^FS\n")

            append("^A0N,28,28\n")
            append("^FO60,265^FDCANTIDAD ESPERADA^FS\n")
            append("^A0N,28,28\n")
            append("^FO460,265^FDCONTEO REALIZADO^FS\n")

            append("^A0N,80,80\n")
            append("^FO130,305^FD$cantidadEsperada^FS\n")
            append("^A0N,80,80\n")
            append("^FO540,305^FD$conteoRealizado^FS\n")

            append("^FO400,255^GB3,135,3^FS\n")

            append("^XZ\n")
        }
    }
}
