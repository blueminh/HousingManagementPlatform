package sem.hoa.domain.entities;

import lombok.NoArgsConstructor;

import javax.persistence.*;

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
}
