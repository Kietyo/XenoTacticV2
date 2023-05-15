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

            Row().addTo(this) {
                item {
                    Column {
                        item {
                            Text("Hello world")
                        }
                        item {
                            Text("Hello world 2")
                        }
                    }
                }

                item {
                    Column {
                        item {
                            Text("Hello world")
                        }
                        item {
                            Text("Hello world 2")
                        }
                    }.content
                }
            }
//            val column = Column().addTo(this) {
//                item {
//                    Text("Hello world")
//                }
//                item {
//                    Text("Hello world 2")
//                }
//            }

        }
    }
}

interface UILayout {
    val content: Container
}

class Column: UILayout {
    override val content = Container()

    companion object {
        operator fun invoke(fn: Column.() -> Unit): Column {
            val column = Column()
            fn(column)
            column.relayout()
            return column
        }
    }

    fun item(addFn: () -> View) {
        content.addChild(addFn())
    }

    fun item(addFn: () -> UILayout) {
        content.addChild(addFn().content)
    }

    fun relayout() {
        var currHeight = 0f
        content.children.forEach {
            it.y = currHeight
            currHeight += it.height
        }
    }

    fun addTo(stage: Stage, function: Column.() -> Unit): Column {
        content.addTo(stage)
        function(this)
        relayout()
        return this
    }
}

class Row: UILayout {
    override val content = Container()

    fun item(addFn: () -> View) {
        content.addChild(addFn())
    }

    fun item(addFn: () -> UILayout) {
        content.addChild(addFn().content)
    }

    fun relayout() {
        var currWidth = 0f
        content.children.forEach {
            it.x = currWidth
            currWidth += it.width
        }
    }

    fun addTo(stage: Stage, function: Row.() -> Unit): Row {
        content.addTo(stage)
        function(this)
        relayout()
        return this
    }
}