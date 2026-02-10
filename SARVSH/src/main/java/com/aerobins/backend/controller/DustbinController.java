package com.aerobins.backend.controller;

import com.aerobins.backend.entity.Dustbin;
import com.aerobins.backend.service.DustbinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dustbins")
@CrossOrigin(origins = "*")
public class DustbinController {

    @Autowired
    private DustbinService dustbinService;

    @GetMapping
    public ResponseEntity<List<Dustbin>> getAllDustbins() {
        return ResponseEntity.ok(dustbinService.getAllDustbins());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Dustbin> getDustbinById(@PathVariable @NonNull Long id) {
        return dustbinService.getDustbinById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bin/{binId}")
    public ResponseEntity<Dustbin> getDustbinByBinId(@PathVariable String binId) {
        return dustbinService.getDustbinByBinId(binId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Dustbin> createDustbin(@RequestBody @NonNull Dustbin dustbin) {
        Dustbin created = dustbinService.createDustbin(dustbin);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Dustbin> updateDustbin(@PathVariable @NonNull Long id, @RequestBody Dustbin dustbin) {
        try {
            Dustbin updated = dustbinService.updateDustbin(id, dustbin);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDustbin(@PathVariable @NonNull Long id) {
        dustbinService.deleteDustbin(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Dustbin>> getDustbinsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(dustbinService.getDustbinsByStatus(status));
    }

    @GetMapping("/full")
    public ResponseEntity<List<Dustbin>> getFullDustbins(@RequestParam(defaultValue = "80") Integer threshold) {
        return ResponseEntity.ok(dustbinService.getFullDustbins(threshold));
    }

    @GetMapping("/high-odor")
    public ResponseEntity<List<Dustbin>> getHighOdorDustbins(@RequestParam(defaultValue = "7.0") Double threshold) {
        return ResponseEntity.ok(dustbinService.getHighOdorDustbins(threshold));
    }

    @Autowired
    private com.aerobins.backend.service.OdourPredictionService odourPredictionService;

    @GetMapping("/predict")
    public ResponseEntity<String> predictOdour(
            @RequestParam Double temp,
            @RequestParam Double humidity,
            @RequestParam Double gas,
            @RequestParam Double voc,
            @RequestParam Double fill,
            @RequestParam Integer hour) {
        String result = odourPredictionService.predictOdour(temp, humidity, gas, voc, fill, hour);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/sensor-data")
    public ResponseEntity<String> submitSensorData(
            @PathVariable Long id,
            @RequestParam Double temp,
            @RequestParam Double humidity,
            @RequestParam Double gas,
            @RequestParam Double voc,
            @RequestParam Double fill,
            @RequestParam Integer hour) {
        String result = odourPredictionService.predictOdour(temp, humidity, gas, voc, fill, hour, id);
        return ResponseEntity.ok(result);
    }
}
