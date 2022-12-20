package sem.hoa.domain.services;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.HOA;
import sem.hoa.dtos.Pair;
import sem.hoa.exceptions.HoaCreationException;

import java.util.Optional;

@Service
public class HOAService {
    private final transient HOARepository hoaRepository;

    public HOAService(HOARepository hoaRepository) {
        this.hoaRepository = hoaRepository;
    }

    /**
    * Adds the given HOA to the HOA repository.
    *
    * @param hoa = hoa to be added to the repository
    */
    public void createNewHOA(HOA hoa) throws HoaCreationException {
        // TODO do some checks here
        try {
            //System.out.println(hoa.toString());
            if (!hoaRepository.findByHoaName(hoa.getHoaName()).isEmpty()) {
                throw new HoaCreationException("HOA already exists");
            }
            hoaRepository.save(hoa);
            System.out.println("new HOA created:" + hoa.getHoaName());
        } catch (Exception e) {
            System.err.println("HOA was not saved successfully");
            throw new HoaCreationException("HOA was not saved successfully");
        }
    }

    /**
    * Either find the name of the HOA or the hoaID.
    *
    * @param hoaName make this null or empty if hoaID is used
    * @param hoaID   id of the HOA
    * @return the start time and end time of the HOA's board election
    */
    public Pair<Long, Long> findBoardElectionStartTime(@Nullable String hoaName, int hoaID) {
        Optional<HOA> hoa;
        if (hoaName != null) {
            hoa = hoaRepository.findByHoaName(hoaName);
        }  else {
            hoa = hoaRepository.findById(hoaID);
        }
        if (hoa.isEmpty()) {
            return null;
        }
        return new Pair<Long, Long>(hoa.get().getElectionStartTime(), hoa.get().getElectionEndTime());
    }

    public Optional<HOA> findHOAByName(String hoaName) {
        return hoaRepository.findByHoaName(hoaName);
    }

    public Optional<HOA> findHOAByID(int hoaID) {
        return hoaRepository.findById(hoaID);
    }
}
