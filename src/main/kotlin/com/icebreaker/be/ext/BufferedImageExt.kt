package com.icebreaker.be.ext

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

fun BufferedImage.toInputStream(ext: String): InputStream {
    val os = ByteArrayOutputStream()
    ImageIO.write(this, ext.toLowerCase(), os)
    return ByteArrayInputStream(os.toByteArray())
}