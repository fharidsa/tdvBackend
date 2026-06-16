package org.tdv.tdvbackend.integration.tytRfid

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.tdv.tdvbackend.integration.tytRfid.client.TytRfidValidaCajaClient
import org.tdv.tdvbackend.integration.tytRfid.client.TytRfidValidarUsuarioClient
import org.tdv.tdvbackend.integration.tytRfid.dto.TytEmpleadoResponse
import org.tdv.tdvbackend.integration.tytRfid.dto.TytLoginResponse
import org.tdv.tdvbackend.integration.tytRfid.dto.TytValidaCajaResponse

@Service
@ConditionalOnProperty(name = ["tyt-rfid.enabled"], havingValue = "true")
class TytRfidFacadeService(
    private val validaCajaClient: TytRfidValidaCajaClient,
    private val validarUsuarioClient: TytRfidValidarUsuarioClient,
) {

    fun validarUsuarioLogin(login: String, password: String): TytLoginResponse =
        validarUsuarioClient.login(login, password)

    fun obtenerEmpleado(codEmpleado: String): TytEmpleadoResponse? =
        validarUsuarioClient.obtenerEmpleado(codEmpleado)

    fun validarCaja(numCaja: String): TytValidaCajaResponse? =
        validaCajaClient.validarCaja(numCaja)
}
