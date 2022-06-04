package com.xenotactic.gamelogic.ecs

abstract class ECSException(
        messageFn: () -> String
): RuntimeException(messageFn())

class ECSComponentNotFoundException(messageFn: () -> String) : ECSException(messageFn)