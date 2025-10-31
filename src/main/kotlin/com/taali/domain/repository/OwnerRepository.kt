package com.taali.infrastructure.persistence.repository

import com.taali.domain.model.Owner
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
interface OwnerRepository : PanacheRepository<Owner>