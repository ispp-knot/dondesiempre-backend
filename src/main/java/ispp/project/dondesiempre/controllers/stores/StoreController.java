package ispp.project.dondesiempre.controllers.stores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ispp.project.dondesiempre.models.stores.dto.StoresBoundingBoxDTO;
import ispp.project.dondesiempre.services.stores.StoreService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @GetMapping("stores")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<StoresBoundingBoxDTO>> getById(@RequestParam double minLon, @RequestParam double minLat, 
        @RequestParam double maxLon, @RequestParam double maxLat) {
        return new ResponseEntity<>(storeService.findStoresInBoundingBox(minLon, minLat, maxLon, maxLat), HttpStatus.OK);
    }
}
