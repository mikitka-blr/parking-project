package com.example.service;

import com.example.model.ExtraService;
import com.example.repository.ExtraServiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;

    public ExtraServiceService(ExtraServiceRepository extraServiceRepository) {
        this.extraServiceRepository = extraServiceRepository;
    }

    @Transactional
    public ExtraService createService(ExtraService service) {
        return extraServiceRepository.save(service);
    }

    public List<ExtraService> getAllServices() {
        return extraServiceRepository.findAll();
    }

    public ExtraService getServiceById(Long id) {
        return extraServiceRepository.findById(id).orElse(null);
    }

    @Transactional
    public ExtraService updateService(Long id, ExtraService serviceDetails) {
        return extraServiceRepository.findById(id)
            .map(service -> {
                service.setName(serviceDetails.getName());
                service.setPrice(serviceDetails.getPrice());
                return extraServiceRepository.save(service);
            })
            .orElse(null);
    }

    @Transactional
    public boolean deleteService(Long id) {
        if (extraServiceRepository.existsById(id)) {
            extraServiceRepository.deleteById(id);
            return true;
        }
        return false;
    }
}