package com.github.vilmosnagy.elq.testproject.entities

import java.util.*
import javax.persistence.*

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Entity
@Table(name = "Album")
data class Album private constructor(
        @Id @Column(name="AlbumId")         override var id: Int,
        @Version @Column(name = "version")  override var version: Date,
        @Column(name = "title")             var title: String
): IBaseEntity {

    constructor(id: Int, version: Date, title: String, artist: Artist): this(id, version, title) {
        this.artist = artist
    }

    @ManyToOne
    @JoinColumn(name = "artistId", referencedColumnName = "artistId")
    lateinit var artist: Artist
}