package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        if (compensation.getEffectiveDate() == null) {
            compensation.setEffectiveDate(new Date());
        }
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String id) {
        LOG.debug("Creating compensation for employee with id [{}]", id);

        // Sort by effective date
        Sort sort = Sort.by(Sort.Direction.DESC, "effectiveDate");
        Compensation compensation = compensationRepository.findByEmployeeId(id, sort);

        if (compensation == null) {
            throw new RuntimeException("Employee not found with id: " + id);
        }

        return compensation;
    }

    @Override
    public Compensation update(String id, Compensation compensation) {
        LOG.debug("Updating compensation for employee with id [{}]", id);

        return compensationRepository.save(compensation);
    }
}