package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_ASSET_BUILDING_ADMIN")')
class TransferFormController {
    TransferFormService transferFormService
    AssetReviewerService assetReviewerService

    def index(String userId) {
        renderJson(transferFormService.list(userId))
    }

    def show(String userId, Long id) {
        renderJson([form: transferFormService.getFormInfo(id)])
    }

    def save(String userId) {
        TransferFormCommand cmd = new TransferFormCommand()
        bindData(cmd, request.JSON)
        def form = transferFormService.create(userId, cmd)
        renderJson([id: form.id])
    }

    def patch(String userId, Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                transferFormService.submit(userId, cmd)
                break
        }
        renderOk()
    }

    def approvers() {
        renderJson assetReviewerService.checkers
    }

    /**
     * 删除
     */
    def delete(Long id) {
        transferFormService.delete(id)
    }
}
