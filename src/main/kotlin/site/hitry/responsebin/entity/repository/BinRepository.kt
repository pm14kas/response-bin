package site.hitry.responsebin.entity.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import site.hitry.responsebin.entity.Bin
import site.hitry.responsebin.entity.User

interface BinRepository : JpaRepository<Bin, Long> {
    @Query("SELECT bin FROM Bin bin WHERE bin.user = :user ORDER BY bin.createdAt")
    fun findByUser(@Param("user") user: User): List<Bin>

    @Query("SELECT bin FROM Bin bin WHERE bin.id = :id")
    fun findByBinId(@Param("id") id: Long): Bin?
}