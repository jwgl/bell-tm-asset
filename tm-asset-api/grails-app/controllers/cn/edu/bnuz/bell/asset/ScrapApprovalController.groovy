package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_APPROVAL")')
class ScrapApprovalController {
    TransferApprovalService transferApprovalService
    final static List<TransferType> TYPES = [
            TransferType.findByAction('FORBID'),
            TransferType.findByAction('LOSE'),
            TransferType.findByAction('FIX'),
            TransferType.findByAction('REPAIR')
    ]

    def index(String approverId, ListCommand cmd) {
        renderJson transferApprovalService.list(approverId, cmd, TYPES)
    }

    def show(String approverId, Long scrapApprovalId, String id, String type) {
        ListType listType = Enum.valueOf(ListType, type)
        if (id == 'undefined') {
            renderJson transferApprovalService.getFormForReview(approverId, scrapApprovalId, listType, TYPES)
        } else {
            renderJson transferApprovalService.getFormForReview(approverId, scrapApprovalId, listType, UUID.fromString(id), TYPES)
        }
    }

    def patch(String approverId, Long scrapApprovalId, String id, String op) {
        def operation = Event.valueOf(op)
        switch (operation) {
            case Event.ACCEPT:
                def cmd = new AcceptCommand()
                bindData(cmd, request.JSON)
                cmd.id = scrapApprovalId
                transferApprovalService.accept(cmd, approverId, UUID.fromString(id))
                break
            case Event.REJECT:
                def cmd = new RejectCommand()
                bindData(cmd, request.JSON)
                cmd.id = scrapApprovalId
                transferApprovalService.reject(cmd, approverId, UUID.fromString(id))
                break
            default:
                throw new BadRequestException()
        }

        show(approverId, scrapApprovalId, id, 'todo')
    }
}
