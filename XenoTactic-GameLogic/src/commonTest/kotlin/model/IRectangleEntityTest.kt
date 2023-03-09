package model

import com.kietyo.ktruth.assertThat
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.test.Test

internal class IRectangleEntityTest {
    @Test
    fun isFullyCoveredBy() {
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isTrue()

        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(4.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isFalse()

        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isTrue()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(6.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(7.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 6.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 7.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(4.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(3.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 4.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 3.toGameUnit(), 2.toGameUnit(), 2.toGameUnit())
            )
        ).isFalse()

        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isTrue()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(4.toGameUnit(), 5.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isTrue()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 4.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isTrue()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(4.toGameUnit(), 4.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isTrue()

        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(3.toGameUnit(), 3.toGameUnit(), 3.toGameUnit(), 3.toGameUnit())
            )
        ).isFalse()

        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 1.toGameUnit(), 1.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(6.toGameUnit(), 5.toGameUnit(), 1.toGameUnit(), 1.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(5.toGameUnit(), 6.toGameUnit(), 1.toGameUnit(), 1.toGameUnit())
            )
        ).isFalse()
        assertThat(
            RectangleEntity(5.toGameUnit(), 5.toGameUnit(), 2.toGameUnit(), 2.toGameUnit()).isFullyCoveredBy(
                RectangleEntity(6.toGameUnit(), 6.toGameUnit(), 1.toGameUnit(), 1.toGameUnit())
            )
        ).isFalse()

    }
}