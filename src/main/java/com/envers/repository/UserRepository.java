package com.envers.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import com.envers.entity.User;

/**
 * @author abidk
 *
 */
@Repository
public interface UserRepository extends RevisionRepository<User, Long, Integer> , JpaRepository<User, Long>{

}
