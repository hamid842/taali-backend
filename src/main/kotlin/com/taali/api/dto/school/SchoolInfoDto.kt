package com.taali.api.dto.school

import com.taali.domain.model.school.School

data class SchoolInfoDto(
    val id: Long?,
    val name: String,
    val code: String
) {
    companion object {
        fun fromEntity(school: School?): SchoolInfoDto? {
            return school?.let {
                SchoolInfoDto(
                    id = it.id,
                    name = it.name,
                    code = it.code
                )
            }
        }
    }
}