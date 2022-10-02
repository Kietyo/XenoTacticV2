package com.xenotactic.korge.korge_utils

import com.soywiz.korge.view.Text
import com.soywiz.korim.text.TextAlignment
import com.xenotactic.gamelogic.globals.ENTITY_LABEL_SIZE
import com.xenotactic.korge.ui.ENTITY_TEXT_FONT

fun makeEntityLabelText(text: String): Text {
    return Text(text, textSize = ENTITY_LABEL_SIZE,
    font = ENTITY_TEXT_FONT)
}