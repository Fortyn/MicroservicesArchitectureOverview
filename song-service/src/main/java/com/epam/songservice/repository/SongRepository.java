package com.epam.songservice.repository;

import com.epam.songservice.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<SongEntity, Integer> {
}
