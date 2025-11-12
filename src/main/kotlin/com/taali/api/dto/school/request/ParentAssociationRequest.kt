package com.taali.api.dto.school.request

data class ParentAssociationRequest(
    val parentEmails: List<String> = listOf(),
    val newParents: List<NewParentRequest> = listOf(),
    val parents: List<NewParentRequest> = listOf()
)
