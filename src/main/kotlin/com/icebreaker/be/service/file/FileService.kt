package com.icebreaker.be.service.file

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.nio.file.Path

interface FileService {
    fun storeImage(file: MultipartFile, maxWidth: Int, maxHeight: Int): String
    fun storeImage(url: String, maxWidth: Int, maxHeight: Int): String
    fun loadFileAsResource(fileName: String): Resource
    fun loadFileAsPath(fileName: String): Path?
    fun deleteFile(path: Path)
    fun storeImage(path: Path, maxWidth: Int, maxHeight: Int): String
    fun blur(bufferedImage: BufferedImage): BufferedImage
    fun toByteArrayOutputStream(fileName: String, image: BufferedImage): ByteArrayOutputStream
}
