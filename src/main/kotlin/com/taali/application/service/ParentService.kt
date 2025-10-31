package com.taali.domain.service

import com.taali.domain.model.Parents
import com.taali.infrastructure.persistence.repository.ParentRepository
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ParentService(
    private val parentRepository: ParentRepository
) {
    fun createParent(parents: Parents): Unit = parentRepository.persist(parents)
    fun getParent(id: Long): Parents? = parentRepository.findById(id)
    fun updateParent(parents: Parents): Unit = parentRepository.persist(parents)
    fun deleteParent(id: Long) = parentRepository.deleteById(id)
    fun getAllParents(): List<Parents> = parentRepository.listAll()
}



