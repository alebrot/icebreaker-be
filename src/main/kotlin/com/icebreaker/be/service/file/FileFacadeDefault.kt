package com.icebreaker.be.service.file

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FileFacadeDefault(val fileService: FileService) : FileFacade {
    val logger: Logger = LoggerFactory.getLogger(FileFacadeDefault::class.java)

    @Async("taskExecutor")
    override fun deleteImageIfExistsAsync(fileName: String) {
        val path = fileService.loadFileAsPath(fileName)
        if (path != null) {
            fileService.deleteFile(path)
            logger.debug(" $fileName image deleted with success")
        } else {
            logger.debug("unable to delete image $fileName, image not found")
        }
    }
}

interface FileFacade {
    fun deleteImageIfExistsAsync(fileName: String)
}
