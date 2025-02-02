package com.booking.business.shared.service.impl;

import com.booking.business.shared.repository.OverlapRepository;
import com.booking.business.shared.service.OverlapValidationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BlockPropertyOverlapValidationServiceImpl extends OverlapValidationService {

    protected BlockPropertyOverlapValidationServiceImpl(
            @Qualifier("blockPropertyOverlapRepositoryImpl")
            final OverlapRepository repository) {
        super(repository);
    }
}
