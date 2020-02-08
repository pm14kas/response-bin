package site.hitry.responsebin.form

import javax.validation.constraints.NotEmpty

class ResponseTemplateForm {
    @NotEmpty
    var id: Long? = null
    @NotEmpty
    var body: String = "{\"success\":true}"
    @NotEmpty
    var condition: String = String()
    @NotEmpty
    var default: Boolean = false
}