package sem.hoa.domain.services;

import org.springframework.stereotype.Service;

@Service
public class MemberManagementService {
  // TODO to be implemented
  private final transient MemberManagementRepository memberManagementRepository;

  public MemberManagementService(MemberManagementRepository memberManagementRepository) {
    this.memberManagementRepository = memberManagementRepository;
  }
}
