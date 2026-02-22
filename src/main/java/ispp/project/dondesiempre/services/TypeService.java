package ispp.project.dondesiempre.services;

import ispp.project.dondesiempre.models.products.Type;
import ispp.project.dondesiempre.repositories.products.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypeService {

  private final TypeRepository typeRepository;

  @Autowired
  public TypeService(TypeRepository typeRepository) {
    this.typeRepository = typeRepository;
  }

  public Type getTypeById(Integer id) {
    return typeRepository.findById(id).orElse(null);
  }
}
