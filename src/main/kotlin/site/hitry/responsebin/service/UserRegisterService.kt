package site.hitry.responsebin.service

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import site.hitry.responsebin.entity.User
import site.hitry.responsebin.entity.repository.UserRepository
import site.hitry.responsebin.form.UserForm


@Service
class UserRegisterService
(
    val userRepository: UserRepository,
    val passwordEncoder: BCryptPasswordEncoder
) {
    fun saveUser(userForm: UserForm) {
        var user = User()
        user.email = userForm.email.toString()
        user.password = passwordEncoder.encode(userForm.password)
        user.active = true
        user.scope = "ROLE_USER"

        userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}