package sem.voting.communication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HOACommunication {
  private static RestTemplate restTemplate = new RestTemplate();
  public static boolean checkUserIsBoardMember(String username, int hoaID) throws Exception{
    String url = "";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    if (response.getStatusCode().isError() || response.getBody() == null)
      throw new Exception(response.getBody());
    return response.getBody().equals("boardMember");
  }
}
