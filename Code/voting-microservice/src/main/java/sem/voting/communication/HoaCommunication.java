package sem.voting.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.io.FileInputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class HoaCommunication {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String HOAPath = "http://localhost:8086";
    private static final String hoaIdParamName = "hoaId";


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

    /**
     * Send a request and receive a String as response.
     *
     * @param userId    id of the user making the request
     * @param url       the endpoint to sent to
     * @return the body of the response as String if OK or the embedded error message if
     * receiving a error
     */
    @SuppressWarnings("PMD.UseProperClassLoader")
    private static String makeRequest(String userId, String url, String requestBody, Map<String, String> params) throws Exception {
        // Get the secret
        Properties properties = new Properties();
        URL resourceUrl = HoaCommunication.class.getClassLoader().getResource("application.properties");
        properties.load(new FileInputStream(Paths.get(resourceUrl.toURI()).toFile().getPath()));

        // Generate authToken for the given userId
        String authToken = getTokenFromId(userId, properties.getProperty("jwt.secret"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authToken);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response;
        if (params == null) {
            response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
        } else {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
            for (String key : params.keySet()) {
                uriComponentsBuilder.queryParam(key, "{" + key + "}");
            }
            String urlTemplate = uriComponentsBuilder.encode().toUriString();
            response = new RestTemplate().exchange(urlTemplate, HttpMethod.GET, request, String.class, params);
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
     * Check if a user is a board member of a hoa.
     *
     * @param username  username of the user
     * @param hoaId     hoaID
     * @return whether the user is a board member of this hoa
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsBoardMember(String username, int hoaId) throws Exception {
        String url = HOAPath + "/member/findUserRoleByHoaID";
        Map<String, String> params = new HashMap<>();
        params.put(hoaIdParamName, hoaId + "");
        String response = makeRequest(username, url, "", params);
        return objectMapper.readValue(response, String.class).equals("boardMember");
    }

    /**
     * Check if a user is NOT a board member of any HOA.
     *
     * @param username  username of the user
     * @return whether the user is NOT a board member of any HOA
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsNotBoardMemberOfAnyHoa(String username) throws Exception {
        String url = HOAPath + "/member/isaBoardMemberOfAny";
        String response = makeRequest(username, url, "", null);
        return objectMapper.readValue(response, Integer.class) == -1;
    }

    /**
     * Check if a user is a member of a hoa.
     *
     * @param username  username of the user
     * @param hoaId     hoaId
     * @return whether the user is a ember of this hoa
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsMemberOfThisHoa(String username, int hoaId) throws Exception {
        String url = HOAPath + "/member/isMemberOf";
        Map<String, String> params = new HashMap<>();
        params.put(hoaIdParamName, hoaId + "");
        String response = makeRequest(username, url, "", params);
        return objectMapper.readValue(response, Boolean.class);
    }

    /**
     * Get the joining date of a user.
     *
     * @param username  username of this user
     * @param hoaId     the hoaId to check
     * @return the joining date of this user. If the user is not in the HOA, an exception is thrown
     * @throws Exception either because the member is not in the HOA, or response has errors
     */
    public static Long getJoiningDate(String username, int hoaId) throws Exception {
        String url = HOAPath + "/member/joiningDate";
        Map<String, String> params = new HashMap<>();
        params.put(hoaIdParamName, hoaId + "");
        String response = makeRequest(username, url, "", params);
        return objectMapper.readValue(response, Long.class);
    }

    /**
     * Get the joining board date of a user.
     *
     * @param username  username of this user
     * @param hoaId     the hoaId to check
     * @return the board joining date of this user.
     * If the user is not a board member of the HOA, -1 is returned
     * If the user is not in the HOA, an exception is thrown
     * @throws Exception either because the member is not in the HOA, or response has errors
     */
    public static Long getJoiningBoardDate(String username, int hoaId) throws Exception {
        String url = HOAPath + "/member/joiningBoardDate";
        Map<String, String> params = new HashMap<>();
        params.put(hoaIdParamName, hoaId + "");
        String response = makeRequest(username, url, "", params);
        return objectMapper.readValue(response, Long.class);
    }
}
