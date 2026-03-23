package ispp.project.dondesiempre.modules.stores.services;

import ispp.project.dondesiempre.modules.stores.repositories.SocialNetworkRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialNetworkService {
  private final SocialNetworkRepository socialNetworkRepository;

  public List<String> findAllNames() {
    return socialNetworkRepository.findAllByOrderByNameAsc().stream()
        .map(sn -> sn.getName())
        .toList();
  }
}
