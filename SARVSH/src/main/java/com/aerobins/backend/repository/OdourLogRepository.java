package com.aerobins.backend.repository;

import com.aerobins.backend.entity.OdourLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OdourLogRepository extends JpaRepository<OdourLog, Long> {
    List<OdourLog> findByDustbinId(Long dustbinId);
}
