package ispp.project.dondesiempre.modules.stores.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.auth.services.AuthService;
import ispp.project.dondesiempre.modules.common.exceptions.AlreadyExistsException;
import ispp.project.dondesiempre.modules.common.exceptions.InvalidSocialNetworkException;
import ispp.project.dondesiempre.modules.common.exceptions.ResourceNotFoundException;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkDTO;
import ispp.project.dondesiempre.modules.stores.dtos.SocialNetworkUpdateDTO;
import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.models.Store;
import ispp.project.dondesiempre.modules.stores.models.StoreSocialNetwork;
import ispp.project.dondesiempre.modules.stores.repositories.SocialNetworkRepository;
import ispp.project.dondesiempre.modules.stores.repositories.StoreSocialNetworkRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

@ExtendWith(MockitoExtension.class)
public class StoreSocialNetworkServiceTest {

  @Mock private StoreSocialNetworkRepository storeSocialNetworkRepository;

  @Mock private SocialNetworkRepository socialNetworkRepository;

  @Mock private AuthService authService;

  @Mock private ApplicationContext applicationContext;

  @Mock private StoreService storeService;

  @InjectMocks private StoreSocialNetworkService storeSocialNetworkService;

  private UUID storeId;
  private UUID socialNetworkId;
  private UUID relationId;

  private Store store;
  private SocialNetwork socialNetwork;
  private StoreSocialNetwork relation;

  @BeforeEach
  void setUp() {
    storeId = UUID.randomUUID();
    socialNetworkId = UUID.randomUUID();
    relationId = UUID.randomUUID();

    store = new Store();
    store.setId(storeId);

    socialNetwork = new SocialNetwork();
    socialNetwork.setId(socialNetworkId);
    socialNetwork.setName("Instagram");

    relation = new StoreSocialNetwork();
    relation.setId(relationId);
    relation.setStore(store);
    relation.setSocialNetwork(socialNetwork);
    relation.setLink("https://instagram.com/test");
  }

  @Test
  void findByStoreId_shouldReturnRelations_whenStoreExists() {
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(storeSocialNetworkRepository.findByStoreId(storeId)).thenReturn(List.of(relation));

    List<StoreSocialNetwork> result = storeSocialNetworkService.findByStoreId(storeId);

    assertEquals(1, result.size());
    assertEquals(relation, result.get(0));
    verify(applicationContext).getBean(StoreService.class);
    verify(storeService).findById(storeId);
    verify(storeSocialNetworkRepository).findByStoreId(storeId);
  }

  @Test
  void findByStoreId_shouldThrowException_whenStoreDoesNotExist() {
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId))
        .thenThrow(new ResourceNotFoundException("Store not found"));

    assertThrows(
        ResourceNotFoundException.class, () -> storeSocialNetworkService.findByStoreId(storeId));

    verify(applicationContext).getBean(StoreService.class);
    verify(storeService).findById(storeId);
    verify(storeSocialNetworkRepository, never()).findByStoreId(storeId);
  }

  @Test
  void addStoreSocialNetwork_shouldSaveRelation_whenDataIsValid() {
    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);

    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("https://instagram.com/test");

    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Instagram")).thenReturn(Optional.of(socialNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId))
        .thenReturn(false);
    when(storeSocialNetworkRepository.save(
            org.mockito.ArgumentMatchers.any(StoreSocialNetwork.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    StoreSocialNetwork result = storeSocialNetworkService.addStoreSocialNetwork(storeId, dto);

    assertEquals(store, result.getStore());
    assertEquals(socialNetwork, result.getSocialNetwork());
    assertEquals("https://instagram.com/test", result.getLink());

    verify(applicationContext).getBean(StoreService.class);
    verify(storeService).findById(storeId);
    verify(authService).assertUserOwnsStore(store);
    verify(socialNetworkRepository).findByName("Instagram");
    verify(storeSocialNetworkRepository)
        .existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId);
    verify(storeSocialNetworkRepository)
        .save(org.mockito.ArgumentMatchers.any(StoreSocialNetwork.class));
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenSocialNetworkDoesNotExist() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("https://instagram.com/test");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Instagram")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));

    verify(applicationContext).getBean(StoreService.class);
    verify(storeService).findById(storeId);
    verify(authService).assertUserOwnsStore(store);
    verify(socialNetworkRepository).findByName("Instagram");
    verify(storeSocialNetworkRepository, never())
        .existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId);
    verify(storeSocialNetworkRepository, never()).save(any());
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenRelationAlreadyExists() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("https://instagram.com/test");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Instagram")).thenReturn(Optional.of(socialNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId))
        .thenReturn(true);

    assertThrows(
        AlreadyExistsException.class,
        () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));

    verify(applicationContext).getBean(StoreService.class);
    verify(storeService).findById(storeId);
    verify(authService).assertUserOwnsStore(store);
    verify(socialNetworkRepository).findByName("Instagram");
    verify(storeSocialNetworkRepository)
        .existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId);
    verify(storeSocialNetworkRepository, never()).save(any());
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenLinkIsInvalidUrl() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("invalid-url-format");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Instagram")).thenReturn(Optional.of(socialNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId))
        .thenReturn(false);

    assertThrows(
        InvalidSocialNetworkException.class,
        () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenLinkIsInvalidPhone() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Teléfono");
    dto.setLink("123");

    SocialNetwork phoneNetwork = new SocialNetwork();
    phoneNetwork.setId(UUID.randomUUID());
    phoneNetwork.setName("Teléfono");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Teléfono")).thenReturn(Optional.of(phoneNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(
            storeId, phoneNetwork.getId()))
        .thenReturn(false);

    assertThrows(
        InvalidSocialNetworkException.class,
        () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenMismatchedDomain() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Instagram");
    dto.setLink("https://tiktok.com/@midominio");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Instagram")).thenReturn(Optional.of(socialNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(storeId, socialNetworkId))
        .thenReturn(false);

    InvalidSocialNetworkException exception =
        assertThrows(
            InvalidSocialNetworkException.class,
            () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));

    assertEquals("Link must be a valid Instagram URL.", exception.getMessage());
  }

  @Test
  void addStoreSocialNetwork_shouldThrowException_whenWhatsappHasInvalidLink() {
    SocialNetworkDTO dto = new SocialNetworkDTO();
    dto.setName("Whatsapp");
    dto.setLink("https://google.com");

    SocialNetwork whatsappNetwork = new SocialNetwork();
    whatsappNetwork.setId(UUID.randomUUID());
    whatsappNetwork.setName("Whatsapp");

    when(applicationContext.getBean(StoreService.class)).thenReturn(storeService);
    when(storeService.findById(storeId)).thenReturn(store);
    when(socialNetworkRepository.findByName("Whatsapp")).thenReturn(Optional.of(whatsappNetwork));
    when(storeSocialNetworkRepository.existsByStoreIdAndSocialNetworkId(
            storeId, whatsappNetwork.getId()))
        .thenReturn(false);

    InvalidSocialNetworkException exception =
        assertThrows(
            InvalidSocialNetworkException.class,
            () -> storeSocialNetworkService.addStoreSocialNetwork(storeId, dto));

    assertEquals(
        "Link must be a valid WhatsApp URL (wa.me) or phone number.", exception.getMessage());
  }

  @Test
  void update_shouldModifyLink_whenRelationExists() {
    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("https://instagram.com/new-link");

    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.of(relation));
    when(storeSocialNetworkRepository.save(relation)).thenReturn(relation);

    StoreSocialNetwork result = storeSocialNetworkService.update(relationId, dto);

    assertEquals("https://instagram.com/new-link", result.getLink());
    verify(storeSocialNetworkRepository).findByIdWithSocialNetwork(relationId);
    verify(authService).assertUserOwnsStore(store);
    verify(storeSocialNetworkRepository).save(relation);
  }

  @Test
  void update_shouldThrowException_whenRelationDoesNotExist() {
    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("https://instagram.com/new-link");

    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> storeSocialNetworkService.update(relationId, dto));

    verify(storeSocialNetworkRepository).findByIdWithSocialNetwork(relationId);
    verify(authService, never()).assertUserOwnsStore(store);
    verify(storeSocialNetworkRepository, never()).save(relation);
  }

  @Test
  void update_shouldThrowException_whenLinkIsInvalidUrl() {
    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("invalid-url-format");

    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.of(relation));

    assertThrows(
        InvalidSocialNetworkException.class,
        () -> storeSocialNetworkService.update(relationId, dto));
  }

  @Test
  void update_shouldThrowException_whenMismatchedDomain() {
    SocialNetworkUpdateDTO dto = new SocialNetworkUpdateDTO();
    dto.setLink("https://facebook.com/mi-pagina");

    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.of(relation));

    InvalidSocialNetworkException exception =
        assertThrows(
            InvalidSocialNetworkException.class,
            () -> storeSocialNetworkService.update(relationId, dto));

    assertEquals("Link must be a valid Instagram URL.", exception.getMessage());
  }

  @Test
  void delete_shouldRemoveRelation_whenRelationExists() {
    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.of(relation));

    storeSocialNetworkService.delete(relationId);

    verify(storeSocialNetworkRepository).findByIdWithSocialNetwork(relationId);
    verify(authService).assertUserOwnsStore(store);
    verify(storeSocialNetworkRepository).delete(relation);
  }

  @Test
  void delete_shouldThrowException_whenRelationDoesNotExist() {
    when(storeSocialNetworkRepository.findByIdWithSocialNetwork(relationId))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> storeSocialNetworkService.delete(relationId));

    verify(storeSocialNetworkRepository).findByIdWithSocialNetwork(relationId);
    verify(authService, never()).assertUserOwnsStore(store);
    verify(storeSocialNetworkRepository, never()).delete(relation);
  }
}
