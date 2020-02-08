package site.hitry.responsebin.entity.repository

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.ResponseTemplate

interface ResponseTemplateRepository : JpaRepository<ResponseTemplate, Long> {
    @Query("SELECT rt FROM ResponseTemplate rt WHERE rt.bin = :bin ORDER BY rt.isDefault ASC, rt.priority DESC")
    fun findByBin(@Param("bin") bin: Bin, pageable: Pageable): List<ResponseTemplate>
}