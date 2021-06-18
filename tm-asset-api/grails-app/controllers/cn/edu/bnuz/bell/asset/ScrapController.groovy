package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_CENTER_ADMIN")')
class ScrapController {
    TransferFormService transferFormService
    AssetReviewerService assetReviewerService
    SecurityService securityService
    FileTransferService fileTransferService

    def index() {
        renderJson(transferFormService.list(securityService.userId))
    }

    def show(Long id) {
        renderJson([form: transferFormService.getFormInfo(id)])
    }

    def save() {
        TransferFormCommand cmd = new TransferFormCommand()
        bindData(cmd, request.JSON)
        def form = transferFormService.create(securityService.userId, cmd)
        renderJson([id: form.id])
    }

    def patch(Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                transferFormService.submit(securityService.userId, cmd)
                break
        }
        renderOk()
    }

    def upload() {
        String prefix = params.prefix
        renderJson ([file: fileTransferService.upload(prefix,  request)])
    }

    def approvers() {
        renderJson assetReviewerService.approvers
    }

    /**
     * 删除
     */
    def delete(Long id) {
        transferFormService.delete(id)
        renderOk()
    }
}
