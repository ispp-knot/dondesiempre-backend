package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;

  @Autowired
  public StorefrontService(StorefrontRepository storefrontRepository) {
    this.storefrontRepository = storefrontRepository;
  }

  @Transactional(readOnly = true)
  public Storefront findById(Integer id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }
}
