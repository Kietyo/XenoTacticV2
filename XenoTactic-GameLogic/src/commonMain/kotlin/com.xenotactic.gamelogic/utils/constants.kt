package com.xenotactic.gamelogic.utils

const val GRID_SIZE = 25f

// The border will be a ratio of the grid size.
const val BORDER_RATIO = 0.5f

// Grid lines width based on ratio of the grid size.
const val GRID_LINES_RATIO = 0.04f

// Size of the grid number text based on the ratio of the grid size.
const val GRID_NUMBERS_RATIO = 0.5f

// Path lines width based on ratio of the grid size.
const val PATH_LINES_RATIO = 0.25f

// The width of the line based on the ratio of the grid size.
const val LINE_WIDTH_RATIO = 0.035f

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