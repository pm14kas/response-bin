package site.hitry.responsebin.entity.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.Request

interface RequestRepository : JpaRepository<Request, Long> {
    @Query("SELECT request FROM Request request WHERE request.bin = :bin ORDER BY request.createdAt DESC")
    fun findByBin(@Param("bin") bin: Bin, pageable: Pageable): List<Request>
}