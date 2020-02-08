package site.hitry.responsebin.form

import javax.validation.constraints.Email
import javax.validation.constraints.NotEmpty

class UserForm {
    @NotEmpty
    var password: String? = null
    @NotEmpty
    var passwordConfirm: String? = null
    @Email
    @NotEmpty
    var email: String? = null
    @Email
    @NotEmpty
    var emailConfirm: String? = null
}