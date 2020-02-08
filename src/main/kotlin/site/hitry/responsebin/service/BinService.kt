package site.hitry.responsebin.service

import org.springframework.stereotype.Service
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.ResponseTemplate
import site.hitry.responsebin.entity.User
import site.hitry.responsebin.entity.repository.BinRepository
import site.hitry.responsebin.entity.repository.ResponseTemplateRepository
import site.hitry.responsebin.form.BinForm


@Service
class BinService
(
    val binRepository: BinRepository,
    val responseTemplateRepository: ResponseTemplateRepository
) {
    fun save(bin: Bin): Bin {
        return binRepository.save(bin)
    }

    fun saveBin(binForm: BinForm, user: User) {
        var bin = Bin()
        bin.name = binForm.name.toString()
        bin.type = binForm.type.toString()
        bin.active = true
        bin.user = user

        binRepository.save(bin)

        for (responseForm in binForm.responseForms) {
            var rt = ResponseTemplate()
            rt.bin = bin
            rt.isDefault = responseForm.default
            rt.condition = responseForm.condition
            rt.body = responseForm.body
            responseTemplateRepository.save(rt)
        }

    }

    fun findByUser(user: User): List<Bin> {
        return binRepository.findByUser(user)
    }

    fun findByBinId(id: Long): Bin? {
        return binRepository.findByBinId(id)
    }
}