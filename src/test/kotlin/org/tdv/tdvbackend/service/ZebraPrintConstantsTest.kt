package org.tdv.tdvbackend.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ZebraPrintConstantsTest {

    @Test
    fun userDateLabelDimensions_at300Dpi() {
        assertEquals(378, ZebraPrintConstants.cmToDots(3.2, 300))
        assertEquals(354, ZebraPrintConstants.cmToDots(3.0, 300))
        assertEquals(236, ZebraPrintConstants.cmToDots(2.0, 300))
    }

    @Test
    fun userDateLabelDimensions_at203Dpi() {
        assertEquals(256, ZebraPrintConstants.cmToDots(3.2, 203))
        assertEquals(240, ZebraPrintConstants.cmToDots(3.0, 203))
        assertEquals(160, ZebraPrintConstants.cmToDots(2.0, 203))
    }

    @Test
    fun scaleFrom300_keepsValuesAt300Dpi() {
        assertEquals(32, ZebraPrintConstants.scaleFrom300(32, 300))
        assertEquals(22, ZebraPrintConstants.scaleFrom300(32, 203))
    }

    @Test
    fun conteoLabelDimensions_scaleFrom203() {
        assertEquals(1182, ZebraPrintConstants.scaleFrom203(800, 300))
        assertEquals(800, ZebraPrintConstants.scaleFrom203(800, 203))
    }
}
