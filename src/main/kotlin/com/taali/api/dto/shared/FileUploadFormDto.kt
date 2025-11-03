package com.taali.api.dto.shared

import org.jboss.resteasy.reactive.RestForm
import org.jboss.resteasy.reactive.multipart.FileUpload


data class FileUploadFormDto(
    @field:RestForm("file")
    val file: FileUpload? = null
) {
    fun isEmpty() = file == null
}

