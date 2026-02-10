package com.aerobins.backend.repository;

import com.aerobins.backend.entity.Dustbin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DustbinRepository extends JpaRepository<Dustbin, Long> {

    Optional<Dustbin> findByBinId(String binId);

    List<Dustbin> findByStatus(String status);

    List<Dustbin> findByFillLevelGreaterThan(Integer fillLevel);

    List<Dustbin> findByOdorLevelGreaterThan(Double odorLevel);
}
