package org.salex.raspberry.agent;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Measurement {
    @Id
    @GeneratedValue
    private Long id;

}
