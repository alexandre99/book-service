package com.booking.api.property.controller;

import com.booking.api.property.dto.BlockPropertyRequest;
import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.model.BlockPropertyPageableView;
import com.booking.business.property.service.BlockPropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RequestMapping("/block-property")
@RestController
public class BlockPropertyController {

    private final BlockPropertyService service;

    public BlockPropertyController(final BlockPropertyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody
                                       final BlockPropertyRequest blockPropertyRequest) {

        final var blockProperty = new BlockProperty(
            null,
            blockPropertyRequest.propertyId(),
            blockPropertyRequest.startDate(),
            blockPropertyRequest.endDate()
        );

        final var blockPropertyId = this.service.save(blockProperty);

        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(blockPropertyId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id")
                                       final UUID id,
                                       @RequestBody
                                       final BlockPropertyRequest blockPropertyRequest) {
        final var blockProperty = new BlockProperty(
                id,
                blockPropertyRequest.propertyId(),
                blockPropertyRequest.startDate(),
                blockPropertyRequest.endDate()
        );

        this.service.save(blockProperty);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<BlockPropertyPageableView> findAll(@RequestParam(name = "page", defaultValue = "1")
                                                             final int page,
                                                             @RequestParam(name = "limit", defaultValue = "10")
                                                             final int limit) {
        final var blockPropertyPageableView = this.service.findAll(page, limit);
        return ResponseEntity.ok(blockPropertyPageableView);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlockProperty> findById(@PathVariable("id")
                                                     final UUID id) {
        final var optionalBlockProperty = this.service.findById(id);
        return optionalBlockProperty.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id")
                                            final UUID id) {
        this.service.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
