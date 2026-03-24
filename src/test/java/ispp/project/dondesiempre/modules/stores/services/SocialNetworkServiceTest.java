package ispp.project.dondesiempre.modules.stores.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.modules.stores.models.SocialNetwork;
import ispp.project.dondesiempre.modules.stores.repositories.SocialNetworkRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SocialNetworkServiceTest {
  @Mock private SocialNetworkRepository socialNetworkRepository;

  @InjectMocks private SocialNetworkService socialNetworkService;

  @Test
  void shouldReturnSocialNetworkName() {
    SocialNetwork instagram = new SocialNetwork();
    instagram.setName("Instagram");

    SocialNetwork facebook = new SocialNetwork();
    facebook.setName("Facebook");

    SocialNetwork tiktok = new SocialNetwork();
    tiktok.setName("TikTok");

    when(socialNetworkRepository.findAllByOrderByNameAsc())
        .thenReturn(List.of(facebook, instagram, tiktok));

    List<String> result = socialNetworkService.findAllNames();

    assertEquals(List.of("Facebook", "Instagram", "TikTok"), result);
    verify(socialNetworkRepository).findAllByOrderByNameAsc();
  }
}
