package com.taali.domain.service

import com.taali.domain.model.Owner
import com.taali.infrastructure.persistence.repository.OwnerRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class OwnerService(
    private val ownerRepository: OwnerRepository
) {
    fun createOwner(owner: Owner): Unit = ownerRepository.persist(owner)
    fun getOwner(id: Long): Owner? = ownerRepository.findById(id)
    fun updateOwner(owner: Owner): Unit = ownerRepository.persist(owner)
    fun deleteOwner(id: Long) = ownerRepository.deleteById(id)
    fun getAllOwners(): List<Owner> = ownerRepository.listAll()
}
