package com.icebreaker.be.service.file.impl

import com.icebreaker.be.FileStorageProperties
import com.icebreaker.be.exception.FileNotFoundException
import com.icebreaker.be.ext.toInputStream
import com.icebreaker.be.service.file.FileService
import net.coobird.thumbnailator.Thumbnails
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
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
    val fileExtensionRegex = Regex("(?<=\\.)[a-zA-Z]+\$")
    val defaultExt = "jpg"

    @PostConstruct
    fun init() {
        this.storageLocation = Paths.get(fileStorageProperties.uploadDir)
                .toAbsolutePath()
                .normalize()
    }

    override fun toByteArrayOutputStream(fileName: String, image: BufferedImage): ByteArrayOutputStream {
        val ext = fileExtensionRegex.find(fileName)?.value ?: defaultExt
        val bao = ByteArrayOutputStream()
        ImageIO.write(image, ext, bao)
        return bao
    }

    override fun storeImage(path: Path, maxWidth: Int, maxHeight: Int): String {
        val ext = fileExtensionRegex.find(path.toString())?.value ?: defaultExt
        val urlImage = ImageIO.read(path.toFile())
        val toInputStream = scale(urlImage, maxWidth, maxHeight).toInputStream(ext)
        return generateNameAndStore(ext, toInputStream)
    }

    override fun storeImage(url: String, maxWidth: Int, maxHeight: Int): String {
        val urlInput = URL(url)
        val ext = fileExtensionRegex.find(url)?.value ?: defaultExt
        val urlImage = ImageIO.read(urlInput)
        val toInputStream = scale(urlImage, maxWidth, maxHeight).toInputStream(ext)
        return generateNameAndStore(ext, toInputStream)
    }

    override fun storeImage(file: MultipartFile, maxWidth: Int, maxHeight: Int): String {
        // Normalize file name
        val originalFilename = file.originalFilename ?: throw IllegalArgumentException("originalFilename is null")
        val fileNameCleaned = StringUtils.cleanPath(originalFilename)

        val inputStreamOrig = file.inputStream

        val ext = fileExtensionRegex.find(fileNameCleaned)?.value ?: defaultExt
        val inputStream = scale(inputStreamOrig, ext, maxWidth, maxHeight)

        return generateNameAndStore(ext, inputStream)
    }

    private fun generateNameAndStore(ext: String, inputStream: InputStream): String {
        val fileName = generateUniqueFileName() + "." + ext
        try {
            val targetLocation = storageLocation.resolve(fileName)
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
            return fileName
        } catch (ex: IOException) {
            throw IllegalStateException("Could not store file $fileName. Please try again!", ex)
        }
    }

    override fun loadFileAsResource(fileName: String): Resource {
        try {
            val filePath: Path = storageLocation.resolve(fileName).normalize()
            val resource = UrlResource(filePath.toUri())
            return if (resource.exists()) {
                resource
            } else {
                throw FileNotFoundException("File not found $fileName")
            }
        } catch (ex: MalformedURLException) {
            throw FileNotFoundException("File not found $fileName")
        }
    }

    override fun loadFileAsPath(fileName: String): Path? {
        val filePath: Path = storageLocation.resolve(fileName).normalize()
        return if (Files.exists(filePath)) {
            filePath
        } else {
            null
        }
    }

    override fun deleteFile(path: Path) {
        Files.delete(path)
    }

    private fun generateUniqueFileName(): String {
        return UUID.randomUUID().toString()
    }

    private fun scale(img: BufferedImage, width: Int, height: Int): BufferedImage {
        return Thumbnails.of(img).size(width, height).asBufferedImage()
    }

    private fun scale(img: InputStream, ext: String, width: Int, height: Int): InputStream {
        val image = ImageIO.read(img)
        val scaled = scale(image, width, height)
        return scaled.toInputStream(ext)
    }

    override fun blur(bufferedImage: BufferedImage): BufferedImage {
        val origWidth = bufferedImage.width
        val origHeight = bufferedImage.height
        val image = scale(bufferedImage, 5, 5)
        return scale(image, origWidth, origHeight)
    }
}