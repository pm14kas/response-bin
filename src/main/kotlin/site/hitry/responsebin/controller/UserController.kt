package site.hitry.responsebin.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import site.hitry.responsebin.form.UserForm
import site.hitry.responsebin.service.UserRegisterService
import site.hitry.responsebin.validator.EmailValidator


@Controller
class UserController
(
    var userService: UserRegisterService
) {
    @ModelAttribute("user")
    fun user(): UserForm {
        return UserForm()
    }

    @GetMapping("/login")
    fun login(model: Model, logout: String?): String? {
        if (logout != null) model.addAttribute("message", "You have been logged out successfully.")

        return "login"
    }

    @GetMapping(*["/", "/welcome"])
    fun welcome(model: Model?): String? {
        return "welcome"
    }

    @GetMapping(*["/registration", "/register"])
    fun registration(model: Model): String {
        return "register"
    }

    @PostMapping(*["/registration", "/register"])
    fun registration(@ModelAttribute("user") userForm: UserForm, bindingResult: BindingResult): String? {
        userForm.email = userForm.email.toString().trim()
        userForm.password = userForm.password.toString().trim()
        userForm.emailConfirm = userForm.emailConfirm.toString().trim()
        userForm.passwordConfirm = userForm.passwordConfirm.toString().trim()

        if (userForm.email!!.isEmpty()) {
            bindingResult.rejectValue("email", "email", "'email' field is empty")
        }
        if (userForm.password!!.isEmpty()) {
            bindingResult.rejectValue("password", "password", "'password' field is empty")
        }
        if (userForm.email != userForm.emailConfirm) {
            bindingResult.rejectValue("emailConfirm", "emailConfirm", "'email' must match 'emailConfirm'")
        }
        if (userForm.password!!.isEmpty()) {
            bindingResult.rejectValue("passwordConfirm", "passwordConfirm", "'password' must match 'passwordConfirm'")
        }

        if (!EmailValidator.validate(userForm.email!!)) {
            bindingResult.rejectValue("email", "email", "'email' field contains inappropriate email")
        }

        if (bindingResult.hasErrors()) {
            return "register"
        }

        val userCheck = userService.findByEmail(userForm.email!!)
        if (userCheck != null) {
            bindingResult.rejectValue("email", "email", "There is already an account registered with that email")
        }

        if (bindingResult.hasErrors()) {
            return "register"
        }

        userService.saveUser(userForm)

        return "redirect:/login"
    }
}