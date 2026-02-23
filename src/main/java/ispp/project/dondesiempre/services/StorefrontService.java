package ispp.project.dondesiempre.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;

  @Transactional(readOnly = true)
  public Storefront findById(Integer id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }
}
