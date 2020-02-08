package site.hitry.responsebin.form

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import site.hitry.responsebin.entity.User
import java.util.*

class UserLoginDetails(private val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority>? {
        val collection = Collections.singletonList(SimpleGrantedAuthority(user.scope))

        return collection
    }

    override fun isEnabled(): Boolean {
        return user.active
    }

    override fun getUsername(): String {
        return user.email
    }

    override fun isCredentialsNonExpired(): Boolean {
        return user.active
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun isAccountNonExpired(): Boolean {
        return user.active
    }

    override fun isAccountNonLocked(): Boolean {
        return user.active
    }
}