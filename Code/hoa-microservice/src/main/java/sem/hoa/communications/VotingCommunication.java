package sem.hoa.communications;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sem.hoa.dtos.CastVoteRequestModel;
import sem.hoa.dtos.ProposalDTO;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class VotingCommunication {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String VotingPath = "http://localhost:8083";

    /**
     * Constructs a token from a userId.
     *
     * @param userId userId to create the token for
     * @return a token for communication between microservices
     */
    private static String getTokenFromId(String userId, String jwtSecret) {
        Date nowDate = new Date(Instant.now().toEpochMilli());
        return Jwts.builder().setClaims(new HashMap<>()).setSubject(userId)
                .setIssuedAt(nowDate)
                .setExpiration(Date.from(nowDate.toInstant().plusSeconds(60 * 60)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    /*
     * Send a request and receive a String as response.
     *
     * @param authToken authentication header
     * @param url the endpoint to sent to
     * @return the body of the response as String if OK or the embedded error message if receiving an error
     */
    private static String makeRequest(String userId, String url, String requestBody) throws Exception {
        // Get the secret
        Properties properties = new Properties();
        URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource("application.properties");
        properties.load(new FileInputStream(Paths.get(resourceUrl.toURI()).toFile().getPath()));

        // Generate authToken for the given userId
        String authToken = getTokenFromId(userId, properties.getProperty("jwt.secret"));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response;
        try {
            response = new RestTemplate().exchange(url, HttpMethod.POST, request, String.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
        if (!response.hasBody()) {
            throw new Exception("Response does not have a body");
        }
        if (response.getStatusCode().isError()) {
            throw new Exception(response.getBody());
        }
        return response.getBody();
    }

    /**
     * Find the current election of an HOA.
     *
     * @param userId user requesting the information
     * @param hoaId HOA to enquire
     * @return the id of the proposal to elect new members, -1 if it doesn't exist
     * @throws Exception when things break
     */
    public static int getCurrentElectionId(String userId, int hoaId) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("hoaId", Integer.toString(hoaId));
        String url = VotingPath + "/active";
        String proposals = makeRequest(userId, url, objectMapper.writeValueAsString(params));
        objectMapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
        ProposalDTO[] proposalArr = objectMapper.readValue(proposals, ProposalDTO[].class);
        for (int idx = 0; idx < proposalArr.length; idx++) {
            if (proposalArr[idx].getType().equals("BoardElection")) {
                return proposalArr[idx].getProposalId();
            }
        }
        return -1;
    }

    /**
     * Apply for board elections.
     *
     * @param userId user applying
     * @param hoaId reference HOA
     * @param proposalId corresponding election
     * @throws Exception when things break
     */
    public static void redirectApplyingRequest(String userId, int hoaId, int proposalId) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("option", userId);
        params.put("hoaId", Integer.toString(hoaId));
        params.put("proposalId", Integer.toString(proposalId));
        String url = VotingPath + "/add-option";
        makeRequest(userId, url, objectMapper.writeValueAsString(params));
    }

    public static void redirectVotingRequest(String userId, CastVoteRequestModel info) throws Exception {
        String url = VotingPath + "/vote";
        makeRequest(userId, url, objectMapper.writeValueAsString(info));
    }
}
