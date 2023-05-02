package com.xenotactic.gamelogic.events

import com.xenotactic.gamelogic.pathing.PathSequence
import com.xenotactic.gamelogic.utils.GameUnit

data class UpdatedPathLineEvent(
    val pathSequence: PathSequence?,
    val newPathLength: GameUnit?)