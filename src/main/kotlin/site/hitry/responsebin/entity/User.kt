package site.hitry.responsebin.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "rb_users")
data class User
(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_sequence")
    @SequenceGenerator(
        name = "user_id_sequence",
        sequenceName = "user_id_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    var id: Long = 0,

    @Column(name = "email", nullable = false, unique = true)
    var email: String = String(),

    @Column(name = "password", nullable = false)
    var password: String = String(),

    @Column(name = "scope", length = 64)
    var scope: String = "ROLE_USER",

    @Column(name = "active", nullable = false)
    var active: Boolean = true,

    @Column(name = "last_login")
    var lastLogin: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var bins: List<Bin> = listOf<Bin>(),

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    var updatedAt: LocalDateTime = LocalDateTime.now()
)