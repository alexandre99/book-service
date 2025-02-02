package com.booking.business.shared.service.impl;

import com.booking.business.shared.repository.OverlapRepository;
import com.booking.business.shared.service.OverlapValidationService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class BookingOverlapValidationServiceImpl extends OverlapValidationService {

    protected BookingOverlapValidationServiceImpl(
            @Qualifier("bookingOverlapRepositoryImpl")
            final OverlapRepository repository) {
        super(repository);
    }
}
