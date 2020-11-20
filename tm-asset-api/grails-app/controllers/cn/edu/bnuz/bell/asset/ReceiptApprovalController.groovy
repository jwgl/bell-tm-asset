package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_APPROVAL")')
class ReceiptApprovalController {
    ReceiptApprovalService receiptApprovalService

    def index(String approverId, ListCommand cmd) {
        renderJson receiptApprovalService.list(approverId, cmd)
    }

    def show(String approverId, Long receiptApprovalId, String id, String type) {
        ListType listType = Enum.valueOf(ListType, type)
        if (id == 'undefined') {
            renderJson receiptApprovalService.getFormForReview(approverId, receiptApprovalId, listType)
        } else {
            renderJson receiptApprovalService.getFormForReview(approverId, receiptApprovalId, listType, UUID.fromString(id))
        }
    }

    def patch(String approverId, Long receiptApprovalId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = receiptApprovalId
                receiptApprovalService.accept(cmd, approverId, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = receiptApprovalId
                receiptApprovalService.reject(cmd, approverId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(approverId, receiptApprovalId, id, 'todo')
    }
}
