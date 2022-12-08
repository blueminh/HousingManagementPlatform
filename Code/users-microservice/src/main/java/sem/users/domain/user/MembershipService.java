package sem.users.domain.user;

import org.springframework.stereotype.Service;

/**
* A DDD service for registering/updating memberships.
 */
@Service
public class MembershipService {
    private final transient UserRepository userRepository;

    public MembershipService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Updates an existing membership of a user for an HOA.
     *
     * @param username username of the user
     *
     * @param hoaMembership the new membership
     * @throws Exception if the user does not already have a membership for that HOA
     */
    public void updateMembership(Username username, HoaMembership hoaMembership)throws Exception {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        } else {
            AppUser olduser = userRepository.deleteAppUserByUsername(username).get();
            if (olduser.updateHoa(hoaMembership)) {
                userRepository.save(olduser);
                return;
            } else {
                throw new MembershipNotFoundException(username, hoaMembership);
            }

        }
    }

    /**
     * Adds a membershing to a user.
     *
     * @param username username
     *
     * @param hoaMembership membership to add
     */
    public void addMembership(Username username, HoaMembership hoaMembership)throws Exception {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException(username);
        } else {
            AppUser olduser = userRepository.deleteAppUserByUsername(username).get();
            if (!olduser.addMembership(hoaMembership)) {
                throw new DuplicateMembershipException(username, hoaMembership);
            }
        }
    }
}
