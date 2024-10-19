package org.awesoma.backend.db.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@Builder
//@NoArgsConstructor
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
  @Id
  @Column(name = "username", nullable = false, unique = true)
  private String username;
  @Column(name = "password", nullable = false)
  private String password;


}


