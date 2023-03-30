package com.xenotactic.korge.ui

import korlibs.logger.Logger
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onScroll
import com.soywiz.korge.input.onUpOutside
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ScalingOption
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alignLeftToLeftOf
import com.soywiz.korge.view.container
import com.soywiz.korge.view.scaleWhileMaintainingAspect
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.io.async.Signal
import korlibs.io.async.launchImmediately
import com.xenotactic.korge.korge_utils.isScrollDown
import com.xenotactic.korge.korge_utils.isScrollUp
import kotlinx.coroutines.Dispatchers
import kotlin.math.max
import kotlin.math.min

data class DropdownOptionClickedEvent(
    val newOption: UIDropdownOption
)

sealed class UIDropdownOption {
    abstract val text: String

    object Empty : UIDropdownOption() {
        override val text: String
            get() = "NULL"
    }

    data class NumberOption(
        override val text: String,
        val data: Number
    ) : UIDropdownOption()

    data class StringOption(
        override val text: String,
        val data: String
    ) : UIDropdownOption()
}

data class UIDropdownSettings(
    val dropdownWidth: Double = 100.0,
    val dropdownHeight: Double = 20.0,
    val dropdownEntryTextPaddingLeft: Double = 5.0,
)

inline fun Container.uiDropdown(
    initialOptions: List<UIDropdownOption> = emptyList(),
    settings: UIDropdownSettings = UIDropdownSettings()
): UIDropdown = UIDropdown(initialOptions, settings).addTo(this)

class UIDropdown(
    initialOptions: List<UIDropdownOption> = emptyList(),
    settings: UIDropdownSettings = UIDropdownSettings(),
) : Container() {
    val dropdownWidth: Double = settings.dropdownWidth
    val dropdownHeight: Double = settings.dropdownHeight
    val dropdownEntryTextPaddingLeft: Double = settings.dropdownEntryTextPaddingLeft

    fun Container.uiDropDownEntry(
        entryWidth: Double,
        entryHeight: Double,
        textPaddingLeft: Double,
        data: IndexedValue<UIDropdownOption>
    ): UIDropDownEntry = UIDropDownEntry(
        entryWidth, entryHeight, textPaddingLeft,
        data
    ).addTo(this)

    class UIDropDownEntry(
        val entryWidth: Double,
        val entryHeight: Double,
        val textPaddingLeft: Double,
        var data: IndexedValue<UIDropdownOption>
    ) : Container() {
        val entryTextWidth = entryWidth - textPaddingLeft
        val currentDropdownText: Text
        val dropdownBg: SolidRect

        init {
            dropdownBg = this.solidRect(entryWidth, entryHeight)
            currentDropdownText = this.text(
                data.value.text, textSize = entryHeight, color = Colors.BLACK,
                autoScaling = true
            ) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(entryTextWidth, entryHeight))
                alignLeftToLeftOf(dropdownBg, padding = textPaddingLeft)
            }
        }

        fun setColor(color: RGBA) {
            dropdownBg.color = color
        }

        fun setOption(option: IndexedValue<UIDropdownOption>) {
            data = option
            currentDropdownText.text = option.value.text
        }
    }

    var options = emptyList<IndexedValue<UIDropdownOption>>()
    val mainEntryDropdown: UIDropDownEntry
    var isOpen = false
    var currWindowIndex = 0
    val openedDropdownContainer: Container
    val dropdownEntries = mutableListOf<UIDropDownEntry>()
    val MAX_NUM_ENTRIES_VISIBLE = 10

    val UNSELECTED_ENTRY_COLOR = Colors.WHITE
    val SELECTED_COLOR = Colors.YELLOW

    val onDropdownChange = Signal<DropdownOptionClickedEvent>()

    inline fun onDropdownChange(noinline handler: suspend (DropdownOptionClickedEvent) -> Unit) {
        onDropdownChange.add {
            launchImmediately(Dispatchers.Default) {
                handler(it)
            }
        }
    }

    fun resetWithOptions(newOptions: List<UIDropdownOption>) {
        currWindowIndex = 0
        logger.info {
            """
                Previous options size: ${options.size}
                new options size: ${newOptions.size}
                newOptions: $newOptions
            """.trimIndent()
        }
        options = newOptions.withIndex().toList()
        mainEntryDropdown.setOption(options.firstOrNull() ?: EMPTY_OPTION)
        dropdownEntries.clear()
        openedDropdownContainer.apply {
            removeChildren()
            for ((i, option) in options.withIndex()) {
                if (i >= MAX_NUM_ENTRIES_VISIBLE) break
                val curr = this.uiDropDownEntry(
                    dropdownWidth, dropdownHeight,
                    dropdownEntryTextPaddingLeft,
                    option
                )
                curr.onClick {
                    logger.info {
                        "Clicked option: $option"
                    }
                    updateMainEntry(curr.data)
                    closeDropdown()
                    setSelectedDropdownEntryColorIfExists()
                }
                curr.y += (dropdownHeight + 5) * i
                dropdownEntries.add(curr)
            }
        }
        openedDropdownContainer.removeFromParent()
        setSelectedDropdownEntryColorIfExists()
        logger.info {
            """
                currWindowIndex: $currWindowIndex
            """.trimIndent()
        }
    }

    init {
        mainEntryDropdown = this.uiDropDownEntry(
            dropdownWidth,
            dropdownHeight,
            dropdownEntryTextPaddingLeft,
            EMPTY_OPTION
        )

        openedDropdownContainer = this.container()

        resetWithOptions(initialOptions)

        mainEntryDropdown.onClick {
            openDropdown()
        }

        onUpOutside {
            closeDropdown()
        }

        openedDropdownContainer.onScroll {
            if (!isOpen) return@onScroll
            if (it.isScrollDown()) {
                incrementWindowIndex()
            }
            if (it.isScrollUp()) {
                decrementWindowIndex()
            }
            updateDropdownEntries()
        }
    }

    private fun decrementWindowIndex(delta: Int = 1) {
        logger.info {
            """
                decrement index
                currWindowIndex: $currWindowIndex
            """.trimIndent()
        }
        currWindowIndex = max(0, currWindowIndex - delta)
    }

    private fun incrementWindowIndex(delta: Int = 1) {
        logger.info {
            """
                incrementing index
                currWindowIndex: $currWindowIndex
                options.size: ${options.size}
            """.trimIndent()
        }
        currWindowIndex = min(
            max(options.size - MAX_NUM_ENTRIES_VISIBLE - 1, 0),
            currWindowIndex + delta
        )
    }

    fun nextEntry() {
        val nextOption = options[min(mainEntryDropdown.data.index + 1, options.size - 1)]
        updateMainEntry(nextOption)
        incrementWindowIndex()
        updateDropdownEntries()
    }

    fun previousEntry() {
        val previousOption = options[max(mainEntryDropdown.data.index - 1, 0)]
        updateMainEntry(previousOption)
        decrementWindowIndex()
        updateDropdownEntries()
    }

    fun getCurrentOption(): UIDropdownOption {
        return mainEntryDropdown.data.value
    }

    private fun updateMainEntry(option: IndexedValue<UIDropdownOption>) {
        logger.info {
            "Main entry was updated!"
        }
        mainEntryDropdown.setOption(option)
        val event = DropdownOptionClickedEvent(
            option.value
        )
        onDropdownChange(event)
    }

    private fun updateDropdownEntries() {
        for ((i, dropDownEntry) in dropdownEntries.withIndex()) {
            val optionIndex = currWindowIndex + i
            dropDownEntry.apply {
                setOption(options[optionIndex])
            }
        }
        setSelectedDropdownEntryColorIfExists()
    }

    private fun setSelectedDropdownEntryColorIfExists() {
        dropdownEntries.forEach {
            it.setColor(
                if (it.data.index == mainEntryDropdown.data.index)
                    SELECTED_COLOR
                else UNSELECTED_ENTRY_COLOR
            )
        }
    }

    private fun openDropdown() {
        openedDropdownContainer.addTo(this@UIDropdown)
        isOpen = true
    }

    private fun closeDropdown() {
        openedDropdownContainer.removeFromParent()
        isOpen = false
    }

    companion object {
        val logger = Logger<UIDropdown>()
        private val EMPTY_OPTION = IndexedValue(0, UIDropdownOption.Empty)

    }
}