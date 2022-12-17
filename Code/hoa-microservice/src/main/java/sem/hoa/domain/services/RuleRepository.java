package sem.hoa.domain.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sem.hoa.domain.entities.Rule;
import sem.hoa.domain.entities.RuleID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, RuleID> {


}