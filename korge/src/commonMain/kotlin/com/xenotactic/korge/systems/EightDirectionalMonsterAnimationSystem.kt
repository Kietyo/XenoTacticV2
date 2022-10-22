package com.xenotactic.korge.systems

import com.xenotactic.ecs.FamilyConfiguration
import com.xenotactic.ecs.System
import com.xenotactic.ecs.World
import com.xenotactic.korge.components.AnimationComponent
import com.xenotactic.korge.components.ComputedSpeedEffectComponent
import com.xenotactic.korge.components.PathSequenceTraversalComponent
import com.xenotactic.korge.components.UIEightDirectionalSpriteComponent
import kotlin.time.Duration

class EightDirectionalMonsterAnimationSystem(
    val world: World
) : System() {
    override val familyConfiguration: FamilyConfiguration = FamilyConfiguration(
        allOfComponents = setOf(
            UIEightDirectionalSpriteComponent::class,
            PathSequenceTraversalComponent::class,
            AnimationComponent::class
        )
    )

    override fun update(deltaTime: Duration) {
        getFamily().getSequence().forEach {
            val uiEightDirectionalSpriteComponent = world[it, UIEightDirectionalSpriteComponent::class]
            val animationComponent = world[it, AnimationComponent::class]
            val computedSpeedEffectComponent = world[it, ComputedSpeedEffectComponent::class]
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