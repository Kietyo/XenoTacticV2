package utils

import com.kietyo.ktruth.assertThat
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.test.Test

class GameUnitTest {

    @Test
    fun range_until() {
        val range = 0.toGameUnit() until 3.toGameUnit()

        assertThat((-1).toGameUnit() in range).isFalse()
        assertThat(0.toGameUnit() in range).isTrue()
        assertThat(1.toGameUnit() in range).isTrue()
        assertThat(2.toGameUnit() in range).isTrue()
        assertThat(3.toGameUnit() in range).isFalse()
    }

    @Test
    fun range_rangeTo() {
        val range = 0.toGameUnit()..3.toGameUnit()

        assertThat((-1).toGameUnit() in range).isFalse()
        assertThat(0.toGameUnit() in range).isTrue()
        assertThat(1.toGameUnit() in range).isTrue()
        assertThat(2.toGameUnit() in range).isTrue()
        assertThat(3.toGameUnit() in range).isTrue()
        assertThat(4.toGameUnit() in range).isFalse()
    }
}