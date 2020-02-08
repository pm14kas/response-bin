package site.hitry.responsebin.entity

import com.google.gson.JsonElement
import java.time.LocalDateTime
import javax.persistence.*
import javax.servlet.http.HttpServletRequest

@Entity
@Table(name = "rb_response_templates")
data class ResponseTemplate
(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "response_id_sequence")
        @SequenceGenerator(
                name = "response_id_sequence",
                sequenceName = "response_id_sequence",
                allocationSize = 1
        )
        @Column(name = "id", nullable = false, unique = true, updatable = false)
        var id: Long = 0,

        @ManyToOne
        @JoinColumn(name = "bin_id", nullable = false, updatable = false)
        var bin: Bin = Bin(),

        @Column(name = "condition", nullable = false)
        var condition: String = String(),

        @Column(name = "body", nullable = false)
        var body: String = String(),

        @Column(name = "priority", nullable = false)
        var priority: Int = 0,

        @Column(name = "is_default", nullable = false)
        var isDefault: Boolean = true,

        @Column(name = "created_at")
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @Column(name = "updated_at")
        var updatedAt: LocalDateTime = LocalDateTime.now()
) {
        public fun checkCondition(json: JsonElement) : Boolean {
                return true;
        }
}