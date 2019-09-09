package org.salex.raspberry.workshop.data;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Database extends CrudRepository<Measurement, Long> {

}
