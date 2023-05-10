package model

import com.kietyo.ktruth.assertThat
import com.xenotactic.gamelogic.model.IPoint
import com.xenotactic.gamelogic.model.IRectangle
import kotlin.test.Test

internal class IRectangleTest {

    @Test
    fun contains_isInside() {
        val rect = IRectangle(1, 1, 4, 2)
        assertThat(rect.contains(IPoint(1, 1))).isTrue()
        assertThat(rect.contains(IPoint(2, 1))).isTrue()
        assertThat(rect.contains(IPoint(3, 1))).isTrue()
        assertThat(rect.contains(IPoint(4, 1))).isTrue()
        assertThat(rect.contains(IPoint(5, 1))).isTrue()

        assertThat(rect.contains(IPoint(1, 1.5))).isTrue()
        assertThat(rect.contains(IPoint(2, 1.5))).isTrue()
        assertThat(rect.contains(IPoint(3, 1.5))).isTrue()
        assertThat(rect.contains(IPoint(4, 1.5))).isTrue()
        assertThat(rect.contains(IPoint(5, 1.5))).isTrue()

        assertThat(rect.contains(IPoint(1, 3))).isTrue()
        assertThat(rect.contains(IPoint(2, 3))).isTrue()
        assertThat(rect.contains(IPoint(3, 3))).isTrue()
        assertThat(rect.contains(IPoint(4, 3))).isTrue()
        assertThat(rect.contains(IPoint(5, 3))).isTrue()
    }

    @Test
    fun contains_isOutside() {
        val rect = IRectangle(1, 1, 4, 2)
        assertThat(rect.contains(IPoint(1, 0.9))).isFalse()
        assertThat(rect.contains(IPoint(2, 0.9))).isFalse()
        assertThat(rect.contains(IPoint(3, 0.9))).isFalse()
        assertThat(rect.contains(IPoint(4, 0.9))).isFalse()
        assertThat(rect.contains(IPoint(5, 0.9))).isFalse()

        assertThat(rect.contains(IPoint(1, 3.1))).isFalse()
        assertThat(rect.contains(IPoint(2, 3.1))).isFalse()
        assertThat(rect.contains(IPoint(3, 3.1))).isFalse()
        assertThat(rect.contains(IPoint(4, 3.1))).isFalse()
        assertThat(rect.contains(IPoint(5, 3.1))).isFalse()

        assertThat(rect.contains(IPoint(0.9, 2))).isFalse()
        assertThat(rect.contains(IPoint(5.1, 2))).isFalse()
    }
}