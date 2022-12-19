package sem.hoa.domain.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="hoas")
@NoArgsConstructor
public class HOA extends HasEvents {
  @Id
  @GeneratedValue
  @Column(name = "id", nullable = false)
  private int id;

  @Column(name = "hoa_name", nullable = false, unique = true)
  private String hoaName;
  // TODO Add the other fields of the HOA

  @Column(name = "country", nullable = false, unique = false)
  private String country;

  @Column(name = "city", nullable = false, unique = false)
  private String city;

  @Column(name = "election_start_time")
  private Long electionStartTime;

  @Column(name = "election_end_time")
  private Long electionEndTime;

  public HOA(String name, String country, String city){
    this.hoaName = name;
    this.country = country;
    this.city = city;
    //this.electionStartTime = Date.from(Instant.now().plusSeconds(31556926));
  }

  public int getId() {
    return id;
  }

  public String getHoaName() {
    return hoaName;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setHoaName(String hoaName) {
    this.hoaName = hoaName;
  }

  public Long getElectionStartTime() {
    return electionStartTime;
  }

  public Long getElectionEndTime() {
    return electionEndTime;
  }

  public void setElectionStartTime(Long electionStartTime) {
    this.electionStartTime = electionStartTime;
  }

  public void setElectionEndTime(Long electionEndTime) {
    this.electionEndTime = electionEndTime;
  }
}
