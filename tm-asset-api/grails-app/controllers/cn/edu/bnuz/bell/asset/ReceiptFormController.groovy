package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_ADVICE_WRITE")')
class ReceiptFormController {
    ReceiptFormService receiptFormService
    AssetReviewerService assetReviewerService

    def index() {
        renderJson receiptFormService.list()
    }

    def create(){
        renderJson receiptFormService.formForCreate
    }

    def save() {
        ReceiptFormCommand cmd = new ReceiptFormCommand()
        bindData(cmd, request.JSON)
        def form = receiptFormService.create(cmd)
        renderJson([id: form.id])
    }

    def show(Long id) {
        renderJson([form: receiptFormService.getFormInfo(id)])
    }

    def edit(Long id) {
        renderJson receiptFormService.getFormForEdit(id)
    }

    def update(Long id) {
        def cmd = new ReceiptFormCommand()
        bindData(cmd, request.JSON)
        cmd.id = id
        receiptFormService.update(cmd)
        renderOk()
    }

    def patch(Long id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.SUBMIT:
                def cmd = new SubmitCommand()
                bindData(cmd, request.JSON)
                cmd.id = id
                receiptFormService.submit(cmd)
                break
        }
        renderOk()
    }

    def approvers() {
        renderJson assetReviewerService.approvers
    }

    /**
     * 删除
     */
    def delete(Long id) {
        receiptFormService.delete(id)
    }
}
