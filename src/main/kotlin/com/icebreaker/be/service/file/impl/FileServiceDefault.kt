package com.icebreaker.be.service.file.impl

import com.icebreaker.be.FileStorageProperties
import com.icebreaker.be.service.file.FileService
import org.imgscalr.Scalr
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.*
import javax.annotation.PostConstruct
import javax.imageio.ImageIO


@Service
class FileServiceDefault(val fileStorageProperties: FileStorageProperties) : FileService {

    lateinit var storageLocation: Path

    @PostConstruct
    fun init() {
        this.storageLocation = Paths.get(fileStorageProperties.uploadDir)
                .toAbsolutePath()
                .normalize()
    }

    override fun storeFile(file: MultipartFile, maxWidth: Int, maxHeight: Int): String {
        // Normalize file name
        val originalFilename = file.originalFilename ?: throw IllegalArgumentException("originalFilename is null")
        val fileNameCleaned = StringUtils.cleanPath(originalFilename)
        val ext = fileNameCleaned.split(".")[1]
        val fileName = generateUniqueFileName() + "." + ext
        try {
            val targetLocation = storageLocation.resolve(fileName)
            val inputStream = scale(file.inputStream, ext, maxWidth, maxHeight)
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)

            return fileName
        } catch (ex: IOException) {
            throw IllegalStateException("Could not store file $fileName. Please try again!", ex)
        }
    }

    override fun loadFileAsResource(fileName: String): Resource {
        try {
            val filePath = storageLocation.resolve(fileName).normalize()
            val resource = UrlResource(filePath.toUri())
            return if (resource.exists()) {
                resource
            } else {
                throw IllegalStateException("File not found $fileName")
            }
        } catch (ex: MalformedURLException) {
            throw IllegalStateException("File not found $fileName", ex)
        }
    }


    private fun generateUniqueFileName(): String {
        return UUID.randomUUID().toString()
    }

    private fun scaleOld(img: BufferedImage, width: Int, height: Int): BufferedImage {
        val tmp = img.getScaledInstance(width, height, Image.SCALE_DEFAULT)
        val scaled = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val g2d = scaled.createGraphics()
        g2d.drawImage(tmp, 0, 0, null)
        g2d.dispose()
        return scaled
    }

    private fun scale(img: BufferedImage, width: Int, height: Int): BufferedImage {
        return Scalr.resize(img, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH,
                width, height, Scalr.OP_ANTIALIAS)
    }

    private fun scale(img: InputStream, ext: String, width: Int, height: Int): InputStream {
        val image = ImageIO.read(img)
        val scaled = scale(image, width, height)
        val os = ByteArrayOutputStream()
        ImageIO.write(scaled, ext.toLowerCase(), os)
        return ByteArrayInputStream(os.toByteArray())
    }

}