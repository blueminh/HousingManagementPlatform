package sem.hoa.domain.services;

import org.springframework.stereotype.Service;
import sem.hoa.domain.entities.HOA;
import sem.hoa.dtos.HoaModifyDTO;
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
     * Adds the given HOA to the HOA repository.
     *
     * @param hoa = hoa to be added to the repository
     */
    public HOA createNewHOAbyRequest(HoaModifyDTO request) throws HoaCreationException {
        // TODO do some checks here
        try {
            //Checks
            this.checkHoaModifyDTO(request);
            //Creates and checks additionally
            HOA hoa = new HOA(request.hoaName, request.userCountry, request.userCity);
            if (!hoaRepository.findByHoaName(hoa.getHoaName()).isEmpty()) {
                throw new HoaCreationException("HOA already exists");
            }
            //Saves to repository
            hoaRepository.save(hoa);
            System.out.println("new HOA created:" + hoa.getHoaName());
            return hoa;
        } catch (Exception e) {
            System.err.println("HOA was not saved successfully");
            throw new HoaCreationException("HOA was not saved successfully");
        }
    }


    public Optional<HOA> findHOAByName(String hoaName) {
        return hoaRepository.findByHoaName(hoaName);
    }

    public Optional<HOA> findHOAByID(int hoaID) {
        return hoaRepository.findById(hoaID);
    }

    public boolean hoaExistsByName(String hoaName) {
        return !hoaRepository.findByHoaName(hoaName).isEmpty();
    }

    /**
     * Checks if the HoaModifyDTO's fields are filled out correctly.
     *
     * @param request = request to be checked
     */

    public void checkHoaModifyDTO(HoaModifyDTO request) throws Exception {

        //Checks if strings are null
        if (request.hoaName == null || request.userCity == null || request.userCountry == null
                || request.userStreet == null || request.userPostalCode == null) {
            System.err.println("one or more fields Invalid(null)");
            throw new Exception("Fields can not be Invalid(null)");
        }
        //checks if variables are valid
        if (request.hoaName.isBlank() || request.userCity.isBlank() || request.userCountry.isBlank()
                || request.userStreet.isBlank() || request.userPostalCode.isBlank()) {
            System.err.println("one or more fields were Empty");
            throw new Exception("Fields can not be Empty");
        }
        //checks if house number is valid
        if (request.userHouseNumber < 0) {
            System.err.println("house Number was < 0");
            throw new Exception("House Number must be a positive integer");
        }
    }
}
