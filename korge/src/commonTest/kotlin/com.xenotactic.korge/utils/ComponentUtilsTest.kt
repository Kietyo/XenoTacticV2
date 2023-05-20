package com.xenotactic.korge.utils

import com.kietyo.ktruth.assertThat
import com.xenotactic.ecs.StagingEntity
import com.xenotactic.ecs.StatefulEntity
import com.xenotactic.gamelogic.model.GameUnitTuple
import com.xenotactic.gamelogic.model.RectangleEntity
import com.xenotactic.gamelogic.utils.toGameUnit
import kotlin.test.Test

class ComponentUtilsTest {
    @Test
    fun isFullyCoveredBy_partiallyCovered_notFullyCovered() {
        val entity = StagingEntityUtils.createCheckpoint(
            0, GameUnitTuple(4, 1)
        )

        assertThat(
            entity.isFullyCoveredBy(
                listOf(
                    RectangleEntity(
                        2.toGameUnit(), 0.toGameUnit(),
                        4.toGameUnit(), 2.toGameUnit()
                    )
                )
            )
        ).isFalse()
    }

    @Test
    fun isFullyCoveredBy_fullyCoveredBy1Entity_fullyCovered() {
        val entity = StagingEntityUtils.createCheckpoint(
            0, GameUnitTuple(4, 1)
        )

        assertThat(
            entity.isFullyCoveredBy(
                listOf(
                    RectangleEntity(
                        4.toGameUnit(), 1.toGameUnit(),
                        4.toGameUnit(), 2.toGameUnit()
                    ),
                )
            )
        ).isTrue()
    }

    @Test
    fun isFullyCoveredBy_fullyCoveredBy2Entities_fullyCovered() {
        val entity = StagingEntityUtils.createCheckpoint(
            0, GameUnitTuple(4, 1)
        )

        assertThat(
            entity.isFullyCoveredBy(
                listOf(
                    RectangleEntity(
                        2.toGameUnit(), 0.toGameUnit(),
                        4.toGameUnit(), 2.toGameUnit()
                    ),
                    RectangleEntity(
                        2.toGameUnit(), 2.toGameUnit(),
                        4.toGameUnit(), 2.toGameUnit()
                    )

                )
            )
        ).isTrue()
    }
}