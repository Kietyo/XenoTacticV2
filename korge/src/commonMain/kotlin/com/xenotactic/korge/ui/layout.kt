package com.xenotactic.korge.ui

import com.xenotactic.ecs.TypedInjections
import korlibs.korge.view.Container
import korlibs.korge.view.Stage
import korlibs.korge.view.View
import korlibs.korge.view.addTo

sealed interface Modifier {
    data class Spacing(val start: Float = 0f, val between: Float = 0f) : Modifier
}

data class Modifiers(val modifiers: TypedInjections<Modifier> = TypedInjections()) {
    inline fun <reified T : Modifier> getOrDefault(default: () -> T): T {
        val modifier = modifiers.getSingletonOrNull<T>()
        return modifier ?: default()
    }

    companion object {
        fun with(modifier: Modifier): Modifiers {
            val modifiers = Modifiers()
            modifiers.modifiers.setSingletonOrThrow(modifier)
            return modifiers
        }

        val NONE = Modifiers()
    }
}

interface UILayout {
    val content: Container
}

class Column : UILayout {
    override val content = Container()

    companion object {
        operator fun invoke(fn: Column.() -> Unit): Column {
            val column = Column()
            fn(column)
            column.relayout()
            return column
        }
    }

    fun addItem(addFn: () -> View) {
        content.addChild(addFn())
    }

    fun addLayout(addFn: () -> UILayout) {
        content.addChild(addFn().content)
    }

    fun relayout(modifiers: Modifiers = Modifiers.NONE) {
        var i = 0
        val spacing = modifiers.getOrDefault { Modifier.Spacing() }
        var currHeight = spacing.start
        content.children.forEach {
            it.y = currHeight
            currHeight += it.height

            if (i in 1..content.children.indices.last) {
                it.y += spacing.between
                currHeight += spacing.between
            }
            i++
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
        val rowModifier = modifiers.getOrDefault<Modifier.Spacing> {
            Modifier.Spacing()
        }
        content.children.forEach {
            it.x = currWidth

            if (i == 0) {
                it.x += rowModifier.start
                currWidth += rowModifier.start
            }

            if (i <= content.children.indices.endInclusive) {
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