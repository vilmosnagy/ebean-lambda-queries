package com.github.vilmosnagy.elq.testproject.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Entity
@Data
@EqualsAndHashCode(exclude = "artist")
@Table(name = "Album")
public class Album {

    @Id
    @Column(name="AlbumId")
    private int id;

    @Version
    @Column(name = "version")
    private Date version;

    @Column(name = "title")
    private String title;

    @ManyToOne
    @JoinColumn(name = "artistId", referencedColumnName = "artistId")
    private Artist artist;

}
