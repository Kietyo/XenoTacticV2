package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import kotlin.time.Duration

class UIEightDirectionalMonsterAnimationSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            com.xenotactic.gamelogic.components.UIEightDirectionalSpriteComponent::class,
            com.xenotactic.gamelogic.components.PathSequenceTraversalComponent::class,
            com.xenotactic.gamelogic.components.AnimationComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val uiEightDirectionalSpriteComponent =
                world[it, com.xenotactic.gamelogic.components.UIEightDirectionalSpriteComponent::class]
            val animationComponent = world[it, com.xenotactic.gamelogic.components.AnimationComponent::class]
            val computedSpeedEffectComponent =
                world[it, com.xenotactic.gamelogic.components.ComputedSpeedEffectComponent::class]
            animationComponent.cumulatedTimeMillisSinceLastFrame += deltaTime.inWholeMilliseconds
            val baseChangeAfterSpeedEffectTimeMillis =
                animationComponent.baseChangeTimeMillis / computedSpeedEffectComponent.computedSpeedEffect
            while (animationComponent.cumulatedTimeMillisSinceLastFrame >= baseChangeAfterSpeedEffectTimeMillis) {
                uiEightDirectionalSpriteComponent.uiEightDirectionalSprite.incrementFrame()
                animationComponent.cumulatedTimeMillisSinceLastFrame -= baseChangeAfterSpeedEffectTimeMillis
            }
        }
    }
}