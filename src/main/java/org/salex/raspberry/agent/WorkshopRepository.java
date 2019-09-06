package org.salex.raspberry.agent;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface WorkshopRepository extends CrudRepository<Measurement, Long> {

}
