import com.xenotactic.ecs.TypedInjections
import com.xenotactic.gamelogic.utils.toScale
import korlibs.korge.view.*
import korlibs.image.color.Colors
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.rootLocalVfs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.math.geom.Size
import kotlin.jvm.JvmStatic

object DebugMain {
    val RESOURCES_FOLDER = rootLocalVfs["XenoTactic-Korge/src/commonMain/resources"]

    @JvmStatic
    fun main(args: Array<String>) = runBlockingNoJs {
        Korge(
            KorgeConfig(
                backgroundColor = Colors["#2b2b2b"],
                virtualSize = Size(1280, 720)
            )
        ) {

            //            Row().addTo(this) {
            //                addLayout {
            //                    Column {
            //                        addItem {
            //                            Text("Hello world")
            //                        }
            //                        addItem {
            //                            Text("Hello world 2")
            //                        }
            //                    }
            //                }
            //
            //                addItem {
            //                    Column {
            //                        addItem {
            //                            Text("Hello world")
            //                        }
            //                        addItem {
            //                            Text("Hello world 2")
            //                        }
            //                    }.content
            //                }
            //            }

            val col = Column().addTo(
                this,
                modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))
            ) {
                addLayout {
                    Row(modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))) {
                        addItem { Text("User Name") }
                        addItem { Text("Kills") }
                        addItem { Text("Damage") }
                    }
                }

                addLayout {
                    Row(modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))) {
                        addItem { Text("Xenotactic") }
                        addItem { Text("13") }
                        addItem { Text("123456") }
                    }
                }
            }

//            col.content.scale = 3.toScale()

        }
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

class Row : UILayout, RectBase() {

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

//interface Modifier {
//    companion object : Modifier
//}
