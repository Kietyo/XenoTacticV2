package com.xenotactic.korge.events

import com.xenotactic.gamelogic.model.MapEntity
import korlibs.korge.view.View

object GoldensEntryHoverOutEvent

data class UIEntityClickedEvent(
    val view: View,
    val entity: MapEntity
)

