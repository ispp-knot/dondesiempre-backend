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

    StoreSocialNetwork ssn = new StoreSocialNetwork();
    ssn.setStore(store);
    ssn.setSocialNetwork(socialNetwork);
    ssn.setLink(dto.getLink());

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

    relation.setLink(dto.getLink());

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
