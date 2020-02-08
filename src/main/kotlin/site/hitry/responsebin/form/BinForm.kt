package site.hitry.responsebin.form

import javax.validation.constraints.NotEmpty

class BinForm {
    @NotEmpty
    var id: Long? = null
    @NotEmpty
    var name: String? = null
    @NotEmpty
    var type: String? = null
    @NotEmpty
    var active: Boolean? = true
    @NotEmpty
    var responseForms: MutableList<ResponseTemplateForm> = mutableListOf();

    companion object {
        const val BIN_TYPE_BODY: String = "body"
        const val BIN_TYPE_PARAMS: String = "params"
    }
}