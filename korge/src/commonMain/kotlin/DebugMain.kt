import com.xenotactic.korge.ui.Column
import com.xenotactic.korge.ui.Modifier
import com.xenotactic.korge.ui.Modifiers
import com.xenotactic.korge.ui.Row
import korlibs.image.color.Colors
import korlibs.io.async.runBlockingNoJs
import korlibs.io.file.std.rootLocalVfs
import korlibs.korge.Korge
import korlibs.korge.KorgeConfig
import korlibs.korge.ui.UIText
import korlibs.korge.ui.uiText
import korlibs.korge.view.container
import korlibs.korge.view.solidRect
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

            val content = container {
                solidRect(Size(100, 100))
                uiText("UI test")
            }

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
                modifiers = Modifiers.of(Modifier.SpacingBetween(between = 10f))
            ) {
                addLayout {
                    Row(modifiers = Modifiers.of(Modifier.SpacingBetween(between = 10f))) {
                        addItem { UIText("User Name") }
                        addItem { UIText("Kills") }
                        addItem { UIText("Damage") }
                    }
                }

                //                addLayout {
                //                    Row(modifiers = Modifiers.with(Modifier.Spacing(start = 10f, between = 20f))) {
                //                        addItem { Text("Xenotactic") }
                //                        addItem { Text("13") }
                //                        addItem { Text("123456") }
                //                    }
                //                }
            }

            //            col.content.scale = 3.toScale()

        }
    }
}

//interface Modifier {
//    companion object : Modifier
//}
