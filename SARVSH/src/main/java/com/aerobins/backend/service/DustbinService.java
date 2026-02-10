package com.aerobins.backend.service;

import com.aerobins.backend.entity.Dustbin;
import com.aerobins.backend.repository.DustbinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DustbinService {

    @Autowired
    private DustbinRepository dustbinRepository;

    public List<Dustbin> getAllDustbins() {
        return dustbinRepository.findAll();
    }

    public Optional<Dustbin> getDustbinById(@NonNull Long id) {
        return dustbinRepository.findById(id);
    }

    public Optional<Dustbin> getDustbinByBinId(String binId) {
        return dustbinRepository.findByBinId(binId);
    }

    public Dustbin createDustbin(@NonNull Dustbin dustbin) {
        return dustbinRepository.save(dustbin);
    }

    public Dustbin updateDustbin(@NonNull Long id, Dustbin dustbinDetails) {
        Dustbin dustbin = dustbinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dustbin not found with id: " + id));

        dustbin.setLocation(dustbinDetails.getLocation());
        dustbin.setLatitude(dustbinDetails.getLatitude());
        dustbin.setLongitude(dustbinDetails.getLongitude());
        dustbin.setFillLevel(dustbinDetails.getFillLevel());
        dustbin.setOdorLevel(dustbinDetails.getOdorLevel());
        dustbin.setStatus(dustbinDetails.getStatus());

        return dustbinRepository.save(dustbin);
    }

    public void deleteDustbin(@NonNull Long id) {
        dustbinRepository.deleteById(id);
    }

    public List<Dustbin> getDustbinsByStatus(String status) {
        return dustbinRepository.findByStatus(status);
    }

    public List<Dustbin> getFullDustbins(Integer threshold) {
        return dustbinRepository.findByFillLevelGreaterThan(threshold);
    }

    public List<Dustbin> getHighOdorDustbins(Double threshold) {
        return dustbinRepository.findByOdorLevelGreaterThan(threshold);
    }
}
