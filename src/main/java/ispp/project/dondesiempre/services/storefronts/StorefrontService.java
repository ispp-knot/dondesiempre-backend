package ispp.project.dondesiempre.services.storefronts;

import ispp.project.dondesiempre.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.models.storefronts.Storefront;
import ispp.project.dondesiempre.repositories.storefronts.StorefrontRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StorefrontService {
  private final StorefrontRepository storefrontRepository;

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public Storefront findById(Integer id) throws ResourceNotFoundException {
    return storefrontRepository
        .findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Storefront with ID " + id + " not found."));
  }
}
