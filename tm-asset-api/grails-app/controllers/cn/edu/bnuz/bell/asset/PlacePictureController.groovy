package cn.edu.bnuz.bell.asset

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize

import java.nio.file.Files


class PlacePictureController {
    @Value('${bell.filesPath}')
    String filesPath
    FileTransferService fileTransferService

    def show(String id) {
        File file = new File(filesPath, "${id}.${params.format}")
        if (file.exists()) {
            render file: file.newInputStream(), contentType: Files.probeContentType(file.toPath())
        } else {
            render status: HttpStatus.NOT_FOUND
        }
    }

    @PreAuthorize('hasAnyAuthority("PERM_ASSET_PLACE_WRITE", "PERM_ASSET_PLACE_EDIT")')
    def save() {
        String prefix = params.prefix
        renderJson ([file: fileTransferService.upload(prefix,  request)])
    }
}
