package site.hitry.responsebin.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "rb_requests")
data class Request
(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "request_id_sequence")
    @SequenceGenerator(
        name = "request_id_sequence",
        sequenceName = "request_id_sequence",
        allocationSize = 1
    )
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    var id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "bin_id", nullable = false, updatable = false)
    var bin: Bin = Bin(),

    @Column(name = "body", nullable = false)
    var body: String = String(),

    @Column(name = "header", nullable = false)
    var header: String = String(),

    @Column(name = "ip", nullable = false)
    var ip: String = String(),

    @Column(name = "method", nullable = false)
    var method: String = String(),

    @Column(name = "commentary", nullable = true)
    var commentary: String? = null,

    @Column(name = "response", nullable = false)
    var response: String = String(),

    @Column(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
)