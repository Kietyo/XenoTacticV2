
enum class ErrorType {
    ERROR1,
    ERROR2,
}

data class EntityIdContext(val entityId: Int)
data class DataContext(val data: String)

sealed class Validator(val errorType: ErrorType) {
    abstract fun hasError(): Boolean

    context(EntityIdContext)
    object CheckError1 : Validator(ErrorType.ERROR1) {
        override fun hasError(): Boolean {
            return entityId == 0
        }
    }

    context(EntityIdContext, DataContext)
    object CheckError2 : Validator(ErrorType.ERROR2) {
        override fun hasError(): Boolean {
            return entityId == 2 && data == "blah"
        }
    }
}

sealed class ValidationResult {
    data class Errors(val errors: List<ErrorType>) : ValidationResult() {
    }
    object Ok : ValidationResult()
}

fun validate(validators: Iterable<Validator>): ValidationResult {
    val errors =
        validators.mapNotNull {
            if (it.hasError()) {
                it.errorType
            } else {
                null
            }
        }
    return if (errors.isEmpty()) ValidationResult.Ok else ValidationResult.Errors(errors)
}

val DEFAULT_VALIDATION = listOf(Validator.CheckError1)

fun checkStuff(entityId: Int, data: String): ValidationResult {
    return with(EntityIdContext(entityId)) {
        validate(
            DEFAULT_VALIDATION + Validator.CheckError2
        )
    }
}