package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import grails.gorm.transactions.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.multipart.MultipartFile

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

}
