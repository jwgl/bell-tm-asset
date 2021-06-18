package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService


class AttachmentController {

    FileTransferService fileTransferService
    SecurityService securityService

    def index(String fileName) {
        fileTransferService.download(fileName, response)
    }
}
