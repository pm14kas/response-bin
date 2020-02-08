package site.hitry.responsebin.validator

object EmailValidator {
    private val emailRegex = Regex("^[\\w-\\.]+@[\\w-\\.]+\\.[\\w]+$")

    fun validate(email: String): Boolean {
        return email.matches(emailRegex)
    }
}