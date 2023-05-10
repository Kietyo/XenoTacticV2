package com.xenotactic.korge.events

import korlibs.korge.view.View
import com.xenotactic.gamelogic.model.MapEntity

object GoldensEntryHoverOutEvent

data class UIEntityClickedEvent(
    val view: View,
    val entity: MapEntity
)

