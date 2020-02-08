package site.hitry.responsebin.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import site.hitry.responsebin.entity.User
import site.hitry.responsebin.entity.repository.UserRepository
import site.hitry.responsebin.form.UserLoginDetails


@Service
class UserLoginDetailsService
(
    val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(email: String?): UserDetails {
        if (null == email || "" == email) {
            throw UsernameNotFoundException("")
        }

        val user = this.userRepository.findByEmail(email) ?: throw UsernameNotFoundException(email)

        return UserLoginDetails(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}