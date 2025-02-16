package com.juansecu.restart.auth.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.juansecu.restart.auth.models.ResendVerificationEmailTrial;

@Repository
public interface IResendVerificationEmailTrialsRepository extends CrudRepository<ResendVerificationEmailTrial, Integer> {}
