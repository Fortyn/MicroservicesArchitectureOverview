package com.epam.songservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "songs", schema = "public")
public class SongEntity {

    @Id
    private Integer id;

    @Column
    private String name;

    @Column
    private String artist;

    @Column
    private String album;

    @Column
    private String duration;

    @Column
    private Integer year;
}
