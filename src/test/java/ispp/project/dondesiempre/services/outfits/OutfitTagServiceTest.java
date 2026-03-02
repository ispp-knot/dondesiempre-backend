package ispp.project.dondesiempre.services.outfits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ispp.project.dondesiempre.models.outfits.OutfitTag;
import ispp.project.dondesiempre.repositories.outfits.OutfitTagRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OutfitTagServiceTest {

  @Mock private OutfitTagRepository outfitTagRepository;

  @InjectMocks private OutfitTagService outfitTagService;

  private UUID OutfitTagID;
  private OutfitTag tag;
  private final String tagName = "casual";

  @BeforeEach
  void setUp() {
    OutfitTagID = UUID.randomUUID(); // Generamos un ID único para cada test
    tag = new OutfitTag();
    tag.setId(OutfitTagID);
    tag.setName("casual");
  }

  @Test
  void findByName_shouldReturnTag_whenTagExists() {
    when(outfitTagRepository.findByName(tagName)).thenReturn(java.util.Optional.of(tag));

    OutfitTag result = outfitTagService.findByName(tagName);
    assertNotNull(result);
    assertEquals(tagName, result.getName());
    verify(outfitTagRepository, times(1)).findByName(tagName);
  }
}
