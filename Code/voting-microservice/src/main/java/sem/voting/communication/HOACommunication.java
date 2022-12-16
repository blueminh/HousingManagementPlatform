package sem.voting.communication;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class HOACommunication {
  private static RestTemplate restTemplate = new RestTemplate();
  public static boolean checkUserIsBoardMember(String username, int hoaID) throws Exception{
    String url = "";
    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
    if (response.getBody() == null)
      throw new Exception("body is null");
    if (response.getStatusCode().isError())
      throw new Exception(response.getBody());
    return response.getBody().equals("boardMember");
  }

  public static boolean checkUserIsNotBoardMemberOfAnyHoa(String username) throws Exception {
    String url = "";
    ResponseEntity<Integer> response = restTemplate.getForEntity(url , Integer.class);
    if (response.getStatusCode().isError() || response.getBody() == null)
      throw new Exception();
    return response.getBody() == -1;
  }

  public static boolean checkUserIsMemberOfThisHOA(String username, int hoaID) throws Exception{
    String url =  "";
    ResponseEntity<Boolean> response = restTemplate.getForEntity(url , Boolean.class);
    if (response.getBody() == null)
      throw new Exception("body is null");
    if (response.getStatusCode().isError())
      throw new Exception("bad request");
    return response.getBody();
  }

  public static Long getJoiningDate(String username, int hoaID) throws Exception {
    String url = "";
    ResponseEntity<Long> response = restTemplate.getForEntity(url , Long.class);
    if (response.getBody() == null)
      throw new Exception("body is null");
    if (response.getStatusCode().isError())
      throw new Exception("bad request");
    return response.getBody();
  }

  public static Long getJoiningBoardDate(String username, int hoaID) throws Exception {
    String url = "";
    ResponseEntity<Long> response = restTemplate.getForEntity(url , Long.class);
    if (response.getBody() == null)
      throw new Exception("body is null");
    if (response.getStatusCode().isError())
      throw new Exception("bad request");
    return response.getBody();
  }
  //TODO: how to ger error message of response with error
}
