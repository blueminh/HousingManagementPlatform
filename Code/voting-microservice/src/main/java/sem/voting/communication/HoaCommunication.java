package sem.voting.communication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;


public class HoaCommunication {
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String HOAPath = "localhost:8085";

    /**
     * Send a request and receive a String as response.
     *
     * @param authToken authentication header
     * @param url the endpoint to sent to
     * @return the body of the response as String if OK or the embedded error message if
     * receiving a error
     */
    private static String makeRequest(String authToken, String url, String requestBody, Map<String, String> params) throws Exception {
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
     * @param username username of the user
     * @param hoaId hoaID
     * @param authToken the authentication token associated with this user
     * @return whether the user is a board member of this hoa
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsBoardMember(String username, int hoaId, String authToken) throws Exception {
        String url = HOAPath + "/member/findUserRoleByHoaID";
        Map<String, String> params = new HashMap<>();
        params.put("hoaId", hoaId + "");
        String response = makeRequest(authToken, url, "", params);
        return objectMapper.readValue(response, String.class).equals("boardMember");
    }

    /**
     * Check if a user is NOT a board member of any HOA.
     *
     * @param username username of the user
     * @param authToken the authentication token associated with this user
     * @return whether the user is NOT a board member of any HOA
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsNotBoardMemberOfAnyHoa(String username, String authToken) throws Exception {
        String url = HOAPath + "/member/isaBoardMemberOfAny";
        String response = makeRequest(authToken, url, "", null);
        return objectMapper.readValue(response, Integer.class) == -1;
    }

    /**
     * Check if a user is a member of a hoa.
     *
     * @param username username of the user
     * @param hoaId hoaId
     * @param authToken the authentication token associated with this user
     * @return whether the user is a ember of this hoa
     * @throws Exception either a bad request or response has error
     */
    public static boolean checkUserIsMemberOfThisHoa(String username, int hoaId, String authToken) throws Exception {
        String url = HOAPath + "/member/isMemberOf";
        Map<String, String> params = new HashMap<>();
        params.put("hoaId", hoaId + "");
        String response = makeRequest(authToken, url, "", params);
        return objectMapper.readValue(response, Boolean.class);
    }

    /**
     * Get the joining date of a user.
     *
     * @param username username of this user
     * @param hoaId the hoaId to check
     * @param authToken the authentication token associated with this user
     * @return the joining date of this user. If the user is not in the HOA, an exception is thrown
     * @throws Exception either because the member is not in the HOA, or response has errors
     */
    public static Long getJoiningDate(String username, int hoaId, String authToken) throws Exception {
        String url = HOAPath + "/member/joiningDate";
        Map<String, String> params = new HashMap<>();
        params.put("hoaId", hoaId + "");
        String response = makeRequest(authToken, url, "", params);
        return objectMapper.readValue(response, Long.class);
    }

    /**
     * Get the joining board date of a user.
     *
     * @param username username of this user
     * @param hoaId the hoaId to check
     * @param authToken the authentication token associated with this user
     * @return the board joining date of this user.
     * If the user is not a board member of the HOA, -1 is returned
     * If the user is not in the HOA, an exception is thrown
     * @throws Exception either because the member is not in the HOA, or response has errors
     */
    public static Long getJoiningBoardDate(String username, int hoaId, String authToken) throws Exception {
        String url = HOAPath + "/member/joiningBoardDate";
        Map<String, String> params = new HashMap<>();
        params.put("hoaId", hoaId + "");
        String response = makeRequest(authToken, url, "", params);
        return objectMapper.readValue(response, Long.class);
    }
}
