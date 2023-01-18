package sem.voting.domain.proposal;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper static class for Proposal.
 */
public class ProposalHelper {
    /**
     * Returns the number of votes each option got.
     *
     * @return Set of Result (option-number tuple).
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    public static Set<Result> getResults(Proposal proposal, Map<String, Option> votes) {
        checkDeadline(proposal);
        if (proposal.getStatus() != ProposalStage.Ended) {
            return null;
        }
        Set<Result> results = new HashSet<>();
        if (votes.isEmpty() || proposal.getAvailableOptions().isEmpty()) {
            return results;
        }
        Map<Option, Integer> myMap;
        myMap = new HashMap<>();
        for (String user : votes.keySet()) {
            Option choice = votes.get(user);
            int newVal = myMap.getOrDefault(choice, 0) + 1;
            myMap.put(choice, newVal);
        }
        for (Option o : proposal.getAvailableOptions()) {
            int val = myMap.getOrDefault(o, 0);
            results.add(new Result(o, val));
        }
        return results;
    }

    /**
     * Check if the deadline has been reached and update the proposal status accordingly.
     */
    public static void checkDeadline(Proposal proposal) {
        Date now = Date.from(Instant.now());
        if (!now.before(proposal.getVotingDeadline())) {
            // Voting has ended
            proposal.setStatus(ProposalStage.Ended);
        }
    }
}
