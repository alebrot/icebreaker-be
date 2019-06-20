package com.icebreaker.be.service.file

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun storeImage(file: MultipartFile, maxWidth: Int, maxHeight: Int): String
    fun storeImage(url: String, maxWidth: Int, maxHeight: Int): String
    fun loadFileAsResource(fileName: String): Resource
}
