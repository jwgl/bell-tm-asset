package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasRole("ROLE_ASSET_CENTER_ADMIN")')
class TransferApprovalController {
	TransferApprovalService transferApprovalService
    final static List<TransferType> TYPES = [
            TransferType.findByAction('CHECKOUT'),
            TransferType.findByAction('STOP')
    ]

    def index(String approverId, ListCommand cmd) {
        renderJson transferApprovalService.list(approverId, cmd, TYPES)
    }

    def show(String approverId, Long transferApprovalId, String id, String type) {
        ListType listType = Enum.valueOf(ListType, type)
        if (id == 'undefined') {
            renderJson transferApprovalService.getFormForReview(approverId, transferApprovalId, listType, TYPES)
        } else {
            renderJson transferApprovalService.getFormForReview(approverId, transferApprovalId, listType, UUID.fromString(id), TYPES)
        }
    }

    def patch(String approverId, Long transferApprovalId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = transferApprovalId
                transferApprovalService.accept(cmd, approverId, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = transferApprovalId
                transferApprovalService.reject(cmd, approverId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(approverId, transferApprovalId, id, 'todo')
    }
}
