package com.xenotactic.korge.ui

import com.xenotactic.ecs.TypedInjections
import korlibs.image.color.Colors
import korlibs.image.color.RGBA
import korlibs.korge.view.*
import korlibs.math.geom.Size

sealed interface Modifier {
    fun and(modifier: Modifier): Modifiers {
        return Modifiers.of(this, modifier)
    }

    data class SpacingBetween(val between: Float = 0f) : Modifier
    data class SolidBackgroundColor(val colors: RGBA = Colors.TRANSPARENT) : Modifier
    data class Padding(val left: Float = 0f, val right: Float = 0f,
        val top: Float = 0f, val bottom: Float = 0f) : Modifier
}

data class Modifiers(val modifiers: TypedInjections<Modifier> = TypedInjections()) {
    inline fun <reified T : Modifier> getOrDefault(default: () -> T): T {
        val modifier = modifiers.getSingletonOrNull<T>()
        return modifier ?: default()
    }

    inline fun <reified T : Modifier> getOrNull(): T? {
        return modifiers.getSingletonOrNull<T>()
    }

    fun and(modifier: Modifier): Modifiers {
        val newModifiers = Modifiers()
        newModifiers.modifiers.putAll(modifiers)
        newModifiers.modifiers.setSingletonOrThrow(modifier)
        return newModifiers
    }

    companion object {
        fun of(vararg modifiers: Modifier): Modifiers {
            val modifiersContainer = Modifiers()
            for (modifier in modifiers) {
                modifiersContainer.modifiers.setSingletonOrThrow(modifier)
            }
            return modifiersContainer
        }

        val NONE = Modifiers()
    }
}

interface UILayout {
    val content: Container
}

class Column : UILayout {
    override val content = Container()
    private val innerContent = content.container { }

    companion object {
        operator fun invoke(fn: Column.() -> Unit): Column {
            val column = Column()
            fn(column)
            column.relayout()
            return column
        }
    }

    fun addItem(addFn: () -> View) {
        innerContent.addChild(addFn())
    }

    fun addLayout(addFn: () -> UILayout) {
        innerContent.addChild(addFn().content)
    }

    fun relayout(modifiers: Modifiers = Modifiers.NONE) {
        var i = 0
        val spacing = modifiers.getOrDefault { Modifier.SpacingBetween() }
        var currHeight = 0f
        innerContent.children.forEach {
            it.y = currHeight
            currHeight += it.height

            if (i in 1..innerContent.children.indices.last) {
                it.y += spacing.between
                currHeight += spacing.between
            }
            i++
        }

        val padding = modifiers.getOrNull<Modifier.Padding>()
        if (padding != null) {
            val width = innerContent.children.maxOf { it.width } + padding.left + padding.right
            val height = currHeight + padding.top + padding.bottom
            val rect = SolidRect(Size(width, height), color = Colors.TRANSPARENT)
            content.addChildAt(rect, 0)
            innerContent.x = padding.left
            innerContent.y = padding.top
        }

        val solidBackgroundColor = modifiers.getOrNull<Modifier.SolidBackgroundColor>()
        if (solidBackgroundColor != null) {
            val rect = SolidRect(Size(content.width, content.height), solidBackgroundColor.colors)
            content.addChildAt(rect, 0)
        }
    }

    fun addTo(stage: Stage, modifiers: Modifiers = Modifiers.NONE, function: Column.() -> Unit = {}): Column {
        content.addTo(stage)
        function(this)
        relayout(modifiers)
        return this
    }
}

class Row : UILayout {
    override val content = Container()

    companion object {
        operator fun invoke(
            modifiers: Modifiers = Modifiers.NONE,
            fn: Row.() -> Unit = {}): Row {
            val row = Row()
            fn(row)
            row.relayout(modifiers)
            return row
        }
    }

    fun addItem(addFn: () -> View) {
        content.addChild(addFn())
    }

    fun addLayout(addFn: () -> UILayout) {
        content.addChild(addFn().content)
    }

    fun relayout(modifiers: Modifiers) {
        var currWidth = 0f
        var i = 0
        val rowModifier = modifiers.getOrDefault<Modifier.SpacingBetween> {
            Modifier.SpacingBetween()
        }
        content.children.forEach {
            it.x = currWidth

            if (i in 1..content.children.indices.endInclusive) {
                it.x += rowModifier.between
                currWidth += rowModifier.between
            }

            currWidth += it.width
            i++
        }
    }

    fun addTo(stage: Stage, modifiers: Modifiers = Modifiers.NONE,
        function: Row.() -> Unit = {}): Row {
        content.addTo(stage)
        function(this)
        relayout(modifiers)
        return this
    }
}