package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.common.exceptions.UnauthorizedException;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.repositories.SocialNetworkRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreSocialNetworkService {

  private final StoreSocialNetworkRepository storeSocialNetworkRepository;
  private final SocialNetworkRepository socialNetworkRepository;
  private final AuthService authService;
  private final ApplicationContext applicationContext;

  private static final String PHONE_REGEX = "^\\+?[1-9]\\d{8,14}$";

  private static final String URL_REGEX =
      "^https?://(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}|localhost|(\\d{1,3}\\.){3}\\d{1,3})(:\\d+)?(/[^\\s]*)?$";

  private static final Set<String> PHONE_NETWORKS = Set.of("Teléfono", "Phone");

  private void validateByNetwork(String name, String link) {
    String clean = link.replaceAll("\\s+", "");

    if (PHONE_NETWORKS.contains(name)) {
      String phoneDigits = clean.startsWith("tel:") ? clean.replace("tel:", "") : clean;
      if (!phoneDigits.matches(PHONE_REGEX)) {
        throw new IllegalArgumentException(
            "Must be a valid telephone number, formed by 9-15 digits, prefix allowed.");
      }
    } else {
      if (!link.matches(URL_REGEX)) {
        throw new IllegalArgumentException("Must be a valid URL.");
      }
    }
  }

  private String normalizeLink(String value) {
    if (value == null) {
      return null;
    }

    String clean = value.trim();
    String noSpaces = clean.replaceAll("\\s+", "");
    String phoneDigits = noSpaces.startsWith("tel:") ? noSpaces.replace("tel:", "") : noSpaces;

    if (phoneDigits.matches(PHONE_REGEX)) {
      return "tel:" + phoneDigits;
    }

    if (!clean.startsWith("http://") && !clean.startsWith("https://") && clean.contains(".")) {
      return "https://" + clean;
    }

    return clean;
  }

  @Transactional(readOnly = true, rollbackFor = ResourceNotFoundException.class)
  public List<StoreSocialNetwork> findByStoreId(UUID storeId) throws ResourceNotFoundException {

    applicationContext.getBean(StoreService.class).findById(storeId);

    return storeSocialNetworkRepository.findByStoreId(storeId);
  }

  @Transactional(
      rollbackFor = {
        UnauthorizedException.class,
        ResourceNotFoundException.class,
        AlreadyExistsException.class
      })
  public StoreSocialNetwork addStoreSocialNetwork(UUID storeId, SocialNetworkDTO dto)
      throws UnauthorizedException, ResourceNotFoundException {

    Store store = applicationContext.getBean(StoreService.class).findById(storeId);
    authService.assertUserOwnsStore(store);

    SocialNetwork socialNetwork =
        socialNetworkRepository
            .findByName(dto.getName())
            .orElseThrow(() -> new ResourceNotFoundException("Social network not found"));

    if (storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(
        storeId, socialNetwork.getId())) {
      throw new AlreadyExistsException("La tienda ya tiene esta red social");
    }

    validateByNetwork(socialNetwork.getName(), dto.getLink());

    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setStore(store);
    ssn.setSocialNetwork(socialNetwork);
    ssn.setLink(normalizeLink(dto.getLink()));

    return storeSocialNetworkRepository.save(ssn);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public StoreSocialNetwork update(UUID id, SocialNetworkUpdateDTO dto)
      throws UnauthorizedException, ResourceNotFoundException {

    StoreSocialNetwork relation =
        storeSocialNetworkRepository
            .findByIdWithSocialNetwork(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store social network not found"));

    authService.assertUserOwnsStore(relation.getStore());

    validateByNetwork(relation.getSocialNetwork().getName(), dto.getLink());

    relation.setLink(normalizeLink(dto.getLink()));

    return storeSocialNetworkRepository.save(relation);
  }

  @Transactional(rollbackFor = {UnauthorizedException.class, ResourceNotFoundException.class})
  public void delete(UUID id) throws UnauthorizedException, ResourceNotFoundException {

    StoreSocialNetwork relation =
        storeSocialNetworkRepository
            .findByIdWithSocialNetwork(id)
            .orElseThrow(() -> new ResourceNotFoundException("Store social network not found"));

    authService.assertUserOwnsStore(relation.getStore());

    storeSocialNetworkRepository.delete(relation);
  }
}
