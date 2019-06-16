package com.icebreaker.be.service.file

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun storeFile(file: MultipartFile, maxWidth: Int, maxHeight: Int): String
    fun loadFileAsResource(fileName: String): Resource
}
