package com.chunchun.staybooking.repository;

import com.chunchun.staybooking.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    Authority findAuthorityByUsername(String username);

}
