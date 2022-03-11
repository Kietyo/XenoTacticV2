package globals

const val GRID_SIZE = 25.0

// The border will be a ratio of the grid size.
const val BORDER_RATIO = 0.5

// Grid lines width based on ratio of the grid size.
const val GRID_LINES_RATIO = 0.04

// Size of the grid number text based on the ratio of the grid size.
const val GRID_NUMBERS_RATIO = 0.5

// Path lines width based on ratio of the grid size.
const val PATH_LINES_RATIO = 0.125

// The width of the line based on the ratio of the grid size.
const val LINE_WIDTH_RATIO = 0.035

const val GAME_WIDTH = 20
const val GAME_HEIGHT = 30

val LEFT_CLICK_BUTTON = 0
val RIGHT_CLICK_BUTTON = 0

// TODO: Re-add ui skin?

// In game units
const val PATHING_POINT_PRECISION = 0.01
const val PATHING_RADIUS = 0.05
const val ALLOWABLE_DIRECTION_DIFF = PATHING_POINT_PRECISION + 0.04