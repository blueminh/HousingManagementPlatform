package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.HOA;

import java.util.Optional;

@Service
public class HOAService {
  private final transient HOARepository hoaRepository;

  public HOAService(HOARepository hoaRepository) {
    this.hoaRepository = hoaRepository;
  }

  public void createNewHOA(HOA hoa) {
    // TODO do some checks here
//    hoaRepository.save(hoa);
  }

  public Optional<HOA> findHOAByName(String hoaName){
    return hoaRepository.findByHoaName(hoaName);
  }
}
