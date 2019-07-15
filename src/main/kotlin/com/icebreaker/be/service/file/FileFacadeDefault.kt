package com.icebreaker.be.service.file

import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class FileFacadeDefault(val fileService: FileService) : FileFacade {

    @Async
    override fun deleteImageIfExistsAsync(fileName: String) {
        val path = fileService.loadFileAsPath(fileName)
        if (path != null) {
            fileService.deleteFile(path)
        }
    }
}

interface FileFacade {
    fun deleteImageIfExistsAsync(fileName: String)
}
