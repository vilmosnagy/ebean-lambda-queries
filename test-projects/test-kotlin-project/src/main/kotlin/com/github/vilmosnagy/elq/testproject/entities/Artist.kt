package com.github.vilmosnagy.elq.testproject.entities

import java.util.*
import javax.persistence.*

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Entity
@Table(name = "artist")
data class Artist(
        @Id @Column(name = "artistId")      override var id: Int,
        @Version @Column(name = "version")  override var version: Date,
        @Column(name = "name")              var name: String,
        @OneToMany(mappedBy = "artist")     var albums: MutableList<Album>
): IBaseEntity