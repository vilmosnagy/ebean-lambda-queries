package com.github.vilmosnagy.elq.testproject.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * @author Vilmos Nagy {@literal <vilmos.nagy@outlook.com>}
 */
@Entity
@Data
@EqualsAndHashCode
@Table(name = "artist")
public class Artist {

    @Id
    @Column(name="artistId")
    private int id;

    @Version
    @Column(name = "version")
    private Date version;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "artist")
    private List<Album> albums;
}
