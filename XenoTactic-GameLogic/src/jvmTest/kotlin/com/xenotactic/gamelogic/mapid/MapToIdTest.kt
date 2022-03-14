package com.xenotactic.gamelogic.mapid

import loadGameMapFromGoldensBlocking
import kotlin.test.Test
import kotlin.test.assertEquals


internal class MapToIdTest {

    @Test
    fun calculateId() {
        val gameMap = loadGameMapFromGoldensBlocking("00001.json")

        assertEquals(MapToId.calculateId(gameMap), "b474dbdcb175a45755461e87642dc12fb93d62ae")
    }
}