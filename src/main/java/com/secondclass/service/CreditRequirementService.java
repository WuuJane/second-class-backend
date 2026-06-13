package com.secondclass.service;

import com.secondclass.entity.CreditRequirement;
import java.util.List;

public interface CreditRequirementService {
    List<CreditRequirement> getAllRequirements();
    void saveRequirement(CreditRequirement requirement);
    void deleteRequirement(Long id);
}