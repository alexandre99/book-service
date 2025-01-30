package com.booking.api.property.controller;

import com.booking.api.property.dto.PropertyRequestDTO;
import com.booking.business.property.model.Property;
import com.booking.business.property.model.PropertyPageableView;
import com.booking.business.property.model.PropertyView;
import com.booking.business.property.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/property")
public class PropertyController {

    private final PropertyService service;

    public PropertyController(final PropertyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Property> create(@RequestBody
                                           final PropertyRequestDTO propertyRequestDTO) {
        final var savedPropertyId = this.service.save(
            new Property(
                null,
                propertyRequestDTO.ownerId(),
                propertyRequestDTO.name(),
                propertyRequestDTO.amenities()
            )
        );

        final var location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPropertyId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id")
                                       final UUID id,
                                       @RequestBody
                                       final PropertyRequestDTO propertyRequestDTO) {
        this.service.save(
            new Property(
                id,
                propertyRequestDTO.ownerId(),
                propertyRequestDTO.name(),
                propertyRequestDTO.amenities()
            )
        );
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<PropertyPageableView> findAll(@RequestParam(name = "page", defaultValue = "1")
                                                        final int page,
                                                        @RequestParam(name = "limit", defaultValue = "10")
                                                        final int limit) {
        final var propertyPageableView = this.service.findAll(page, limit);
        return ResponseEntity.ok(propertyPageableView);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyView> findById(@PathVariable("id")
                                                 final UUID id) {
        final var propertyView = this.service.findById(id);
        return propertyView.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PropertyView> disable(@PathVariable("id")
                                                 final UUID id) {
        this.service.disable(id);
        return ResponseEntity.noContent().build();
    }
}
