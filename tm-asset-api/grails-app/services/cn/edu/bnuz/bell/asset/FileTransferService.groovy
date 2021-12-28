package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile
import net.coobird.thumbnailator.Thumbnails
import net.coobird.thumbnailator.util.exif.ExifUtils
import net.coobird.thumbnailator.util.exif.Orientation

import javax.imageio.ImageIO
import javax.imageio.ImageReader
import javax.imageio.stream.ImageInputStream
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Transactional
class FileTransferService {
    @Value('${bell.filesPath}')
    String filesPath
    SecurityService securityService

    def upload(String prefix, HttpServletRequest request) {
        MultipartFile uploadFile = request.getFile('file')
        if (prefix && !uploadFile.empty) {
            def filePath = "${filesPath}"
            def ext = uploadFile.originalFilename.substring(uploadFile.originalFilename.lastIndexOf('.') + 1).toLowerCase()
            def filename = "${prefix}_${UUID.randomUUID()}.${ext}"
            File dir= new File(filePath)
            if (!dir.exists() || dir.isFile()) {
                dir.mkdirs()
            }
            uploadFile.transferTo( new File(filePath, filename) )
            return filename
        } else {
            throw new BadRequestException('Empty file.')
        }
    }

    def uploadKeepFileName(HttpServletRequest request) {
        MultipartFile uploadFile = request.getFile('file')
        if (!uploadFile.empty) {
            def filePath = "${filesPath}"
            def filename = uploadFile.originalFilename
            File dir= new File(filePath)
            if (!dir.exists() || dir.isFile()) {
                dir.mkdirs()
            }
            uploadFile.transferTo( new File(filePath, filename) )
            return filename
        } else {
            throw new BadRequestException('Empty file.')
        }
    }

    def download(String fileName, HttpServletResponse response) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        ZipOutputStream zipFile = new ZipOutputStream(baos)
        File file = new File("${filesPath}/${fileName}")
        if (file?.exists() && file.isFile()) {
            zipFile.putNextEntry(new ZipEntry(fileName))
            file.withInputStream { input -> zipFile << input }
            zipFile.closeEntry()
        }
        zipFile.finish()
        response.setHeader("Content-disposition",
                "attachment; filename=\"" + URLEncoder.encode("${fileName}.zip", "UTF-8") + "\"")
        response.contentType = "application/zip"
        response.outputStream << baos.toByteArray()
    }

    def uploadPicture(String prefix, HttpServletRequest request) {
        MultipartFile uploadFile = request.getFile('file')
        if (prefix && !uploadFile.empty) {
            def filePath = "${filesPath}"
            def ext = uploadFile.originalFilename.substring(uploadFile.originalFilename.lastIndexOf('.') + 1).toLowerCase()
            def filename = "${prefix}_${UUID.randomUUID()}.${ext}"
            def localFile = new File(filePath, "${UUID.randomUUID()}.${ext}")
            if (shouldRotate(uploadFile.inputStream)) {
                Thumbnails.of(uploadFile.inputStream)
                        .height(1110)
                        .keepAspectRatio(true)
                        .toFile(localFile)
            } else {
                Thumbnails.of(uploadFile.inputStream)
                        .width(1110)
                        .keepAspectRatio(true)
                        .toFile(localFile)
            }
            return filename
        } else {
            throw new BadRequestException('Empty file.')
        }
    }

    private def shouldRotate(InputStream is) {
        ImageInputStream iis = ImageIO.createImageInputStream(is)
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis)
        ImageReader reader = readers.next()
        reader.setInput(iis)
        try {
            Orientation orientation = ExifUtils.getExifOrientation(reader, 0);
            if (orientation != null && orientation != Orientation.TOP_LEFT) {
                return true
            }
            return false
        } catch (e) {
            return false
        }
    }

}
