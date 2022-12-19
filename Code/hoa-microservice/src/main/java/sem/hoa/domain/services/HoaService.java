package sem.hoa.domain.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.Hoa;
import sem.hoa.dtos.Pair;

import java.util.Optional;

@Service
public class HoaService {
    private final transient HoaRepository hoaRepository;

    public HoaService(HoaRepository hoaRepository) {
        this.hoaRepository = hoaRepository;
    }

    /**
     * Create a new HOA.
     *
     * @param hoa the hoa
     */
    public void createNewHoa(Hoa hoa) {
        // TODO do some checks here
        try {
            //System.out.println(hoa.toString());
            hoaRepository.save(hoa);
            System.out.println("new Hoa created:" + hoa.getHoaName());
        } catch (Exception e) {
            System.out.println("unable to save new Hoa");
        }
    }

    /**
     * Find either give the name of the Hoa or the hoaID.
     *
     * @param hoaName make this null or empty if hoaID is used
     * @param hoaId   id of the Hoa
     * @return the start time and end time of the Hoa's board election
     */
    public Pair<Long, Long> findBoardElectionStartTime(@Nullable String hoaName, int hoaId) {
        Optional<Hoa> hoa;
        if (hoaName != null) {
            hoa = hoaRepository.findByHoaName(hoaName);
        } else {
            hoa = hoaRepository.findById(hoaId);
        }
        if (hoa.isEmpty()) {
            return null;
        }
        return new Pair<Long, Long>(hoa.get().getElectionStartTime(), hoa.get().getElectionEndTime());
    }

    public Optional<Hoa> findHoaByName(String hoaName) {
        return hoaRepository.findByHoaName(hoaName);
    }

    public Optional<Hoa> findHoaById(int hoaId) {
        return hoaRepository.findById(hoaId);
    }
}
