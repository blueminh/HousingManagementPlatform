package sem.hoa.communications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import sem.hoa.dtos.CastVoteRequestModel;
import sem.hoa.dtos.UserNameHoaIDDTO;
import sem.hoa.dtos.UserNameHoaNameDTO;

public class VotingCommunication {
  private static ObjectMapper objectMapper = new ObjectMapper();

  /***
   * Send a request and receive a String as response
   * @param authToken authentication header
   * @param url the endpoint to sent to
   * @return the body of the response as String if OK or the embedded error message if
   * receiving a error
   */
  private static String makeRequest(String authToken, String url, String requestBody) throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + authToken);
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
    ResponseEntity<String> response = new RestTemplate().exchange(url, HttpMethod.GET, request, String.class);
    if (!response.hasBody())
      throw new Exception("Response does not have a body");
    if (response.getStatusCode().isError())
      throw new Exception(response.getBody());
    return response.getBody();
  }

  public static void redirectApplyingRequest(String authToken, UserNameHoaNameDTO info) throws Exception {
    String url = "";
    makeRequest(authToken, url, objectMapper.writeValueAsString(info));
  }

  public static void redirectVotingRequest(String authToken, CastVoteRequestModel info) throws Exception {
    String url = "";
    makeRequest(authToken, url, objectMapper.writeValueAsString(info));
  }
}
