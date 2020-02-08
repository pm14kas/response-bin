package site.hitry.responsebin.entity.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.hitry.responsebin.entity.User

interface UserRepository : JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE LOWER(user.email) = LOWER(:email)")
    fun findByEmail(@Param("email") email: String): User?
}