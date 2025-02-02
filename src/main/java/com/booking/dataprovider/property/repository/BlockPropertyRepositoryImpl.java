package com.booking.dataprovider.property.repository;

import com.booking.business.property.model.BlockProperty;
import com.booking.business.property.model.BlockPropertyPageableView;
import com.booking.business.property.repository.BlockPropertyRepository;
import com.booking.dataprovider.property.entity.BlockPropertyJpaEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class BlockPropertyRepositoryImpl implements BlockPropertyRepository {

    private final BlockPropertyJpaEntityRepository delegate;

    public BlockPropertyRepositoryImpl(final BlockPropertyJpaEntityRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public UUID save(final BlockProperty blockProperty) {
        final var entity = BlockPropertyJpaEntity.from(blockProperty);
        return delegate.save(entity).getId();
    }

    @Override
    public Optional<BlockProperty> findById(final UUID id) {
        return this.delegate.findBlockPropertyById(id)
                .map(entity -> new BlockProperty(
                        entity.getId(),
                        entity.getProperty().getId(),
                        entity.getStartDate(),
                        entity.getEndDate()
                ));
    }

    @Override
    public BlockPropertyPageableView findAll(final int page, final int limit) {
        final var results = this.delegate.findAllBlockProperty(PageRequest.of(page - 1, limit));
        if(results.isEmpty()) {
            return BlockPropertyPageableView.empty();
        }

        final var blocks = results.stream()
                .map(entity -> new BlockProperty(
                        entity.getId(),
                        entity.getProperty().getId(),
                        entity.getStartDate(),
                        entity.getEndDate()
                )).collect(Collectors.toSet());

        return new BlockPropertyPageableView(
            blocks,
            results.getTotalElements(),
            results.getTotalPages(),
            results.getNumber() + 1
        );
    }

    @Transactional
    @Override
    public void deleteById(final UUID id) {
        this.delegate.deleteById(id);
    }

    @Override
    public boolean existsById(final UUID id) {
        return this.delegate.existsById(id);
    }

}
