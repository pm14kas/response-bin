package site.hitry.responsebin.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "rb_bins")
data class Bin
(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bin_id_sequence")
        @SequenceGenerator(
                name = "bin_id_sequence",
                sequenceName = "bin_id_sequence",
                allocationSize = 1
        )
        @Column(name = "id", nullable = false, unique = true, updatable = false)
        var id: Long = 0,

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false, updatable = false)
        var user: User = User(),

        @OneToMany(mappedBy = "bin", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @OrderBy("createdAt DESC")
        var requests: List<Request> = listOf<Request>(),

        @OneToMany(mappedBy = "bin", cascade = [CascadeType.ALL], fetch = FetchType.EAGER)
        @OrderBy("priority ASC, id ASC")
        var responseTemplates: List<ResponseTemplate> = listOf<ResponseTemplate>(),

        @Column(name = "name", nullable = false)
        var name: String = String(),

        @Column(name = "type", nullable = false)
        var type: String = "temp",

        @Column(name = "active", nullable = false)
        var active: Boolean = true,

        @Column(name = "created_at")
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at")
        var updatedAt: LocalDateTime = LocalDateTime.now()
)