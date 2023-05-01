package com.xenotactic.gamelogic.globals

import com.xenotactic.gamelogic.utils.GameUnit

const val GRID_SIZE = 25.0

// The border will be a ratio of the grid size.
const val BORDER_RATIO = 0.5

// Grid lines width based on ratio of the grid size.
const val GRID_LINES_RATIO = 0.04

// Size of the grid number text based on the ratio of the grid size.
const val GRID_NUMBERS_RATIO = 0.5

// Path lines width based on ratio of the grid size.
const val PATH_LINES_RATIO = 0.25

// The width of the line based on the ratio of the grid size.
const val LINE_WIDTH_RATIO = 0.035

val GAME_WIDTH = GameUnit(20)
val GAME_HEIGHT = GameUnit(30)

const val ENTITY_LABEL_SIZE = 15f

val LEFT_CLICK_BUTTON = 0
val RIGHT_CLICK_BUTTON = 0

// TODO: Re-add ui skin?

// In game units
const val PATHING_POINT_PRECISION = 0.01
const val PATHING_RADIUS = 0.05
const val ALLOWABLE_DIRECTION_DIFF = PATHING_POINT_PRECISION + 0.04