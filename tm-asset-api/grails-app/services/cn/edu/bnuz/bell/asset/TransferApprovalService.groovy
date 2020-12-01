package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.stateMachine.Event
import cn.edu.bnuz.bell.asset.stateMachine.Status
import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.ListCommand
import cn.edu.bnuz.bell.workflow.ListType
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.WorkflowActivity
import cn.edu.bnuz.bell.workflow.WorkflowInstance
import cn.edu.bnuz.bell.workflow.Workitem
import cn.edu.bnuz.bell.workflow.commands.AcceptCommand
import cn.edu.bnuz.bell.workflow.commands.RejectCommand
import grails.gorm.transactions.Transactional

import javax.annotation.Resource
import java.time.LocalDate

@Transactional
class TransferApprovalService {
    TransferFormService transferFormService

    @Resource(name='receiptReviewStateMachine')
    DomainStateMachineHandler domainStateMachineHandler
    LogService logService

    def getCounts(String userId, List<TransferType> types) {
        [
                (ListType.TODO): TransferForm.countByStatusAndTransferTypeInList(State.SUBMITTED, types),
                (ListType.DONE): TransferForm.countByApproverAndTransferTypeInList(Teacher.load(userId), types)
        ]
    }

    def list(String userId, ListCommand cmd, List<TransferType> types) {
        switch (cmd.type) {
            case ListType.TODO:
                return findTodoList(userId, cmd, types)
            case ListType.DONE:
                return findDoneList(userId, cmd, types)
            default:
                throw new BadRequestException()
        }
    }

    def findTodoList(String userId, ListCommand cmd, List<TransferType> types) {
        def forms = TransferForm.executeQuery'''
select new map(
    tf.id as id,
    tf.note as note,
    tf.dateSubmitted as date,
    tf.dateApproved as dateApproved,
    tf.otherInfo as otherInfo,
    tf.status as status,
    o.name as operator,
    a.name as approver,
    concat(fp.building, fp.name) as source,
    concat(tp.building, tp.name) as target ,
    tt.name as type
)
from TransferForm tf
join tf.operator o
left join tf.fromPlace fp
join tf.transferType tt
join tf.toPlace tp
left join tf.approver a
where tf.status = :status and tf.transferType in (:types)
order by tf.dateSubmitted desc
''', [status: State.SUBMITTED, types: types], [offset: cmd.offset, max: cmd.max]
        return [forms: forms, counts: getCounts(userId, types)]
    }

    def findDoneList(String userId, ListCommand cmd, List<TransferType> types) {
        def forms = TransferForm.executeQuery'''
select new map(
    tf.id as id,
    tf.note as note,
    tf.dateSubmitted as dateSubmitted,
    tf.dateApproved as date,
    tf.otherInfo as otherInfo,
    tf.status as status,
    o.name as operator,
    a.name as approver,
    concat(fp.building, fp.name) as source,
    concat(tp.building, tp.name) as target ,
    tt.name as type
)
from TransferForm tf
join tf.operator o
left join tf.fromPlace fp
join tf.transferType tt
join tf.toPlace tp
join tf.approver a
where a.id = :userId and tf.transferType in (:types)
order by tf.dateSubmitted desc
''', [userId: userId, types: types], [offset: cmd.offset, max: cmd.max]
        return [forms: forms, counts: getCounts(userId, types)]
    }

    def getFormForReview(String userId, Long id, ListType type, List<TransferType> types) {
        def form = transferFormService.getFormInfo(id)

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(form.workflowInstanceId),
                WorkflowActivity.load("${TransferForm.WORKFLOW_ID}.${Activities.APPROVE}"),
                User.load(userId),
        )
        if (workitem) {
            form.workitemId = workitem.id
        }

        return [
                form: form,
                counts: getCounts(userId, types),
                workitemId: workitem ? workitem.id : null,
        ]

    }

    def getFormForReview(String userId, Long id, ListType type, UUID workitemId, List<TransferType> types) {
        def form = transferFormService.getFormInfo(id)

        return [
                form: form,
                counts: getCounts(userId, types),
                workitemId: workitemId,
        ]
    }

    void accept(AcceptCommand cmd, String userId, UUID workitemId) {
        TransferForm transferForm = TransferForm.get(cmd.id)
        domainStateMachineHandler.accept(transferForm, userId, Activities.APPROVE, cmd.comment, workitemId)
        transferForm.approver = Teacher.load(userId)
        transferForm.dateApproved = LocalDate.now()
        transferForm.items.each { item ->
            if (!item.asset.canAction(transferForm.transferType.action as Event, transferForm.toPlace.placeType.state as Status)) {
                throw new BadRequestException("设备当前状态不允许${transferForm.transferType.name}或不可以流转到目标房间")
            }
            item.asset.state =  transferForm.toPlace.placeType.state as Status
            item.asset.room = transferForm.toPlace
        }
        logService.log(transferForm.transferType.name, "批准#${transferForm.id}", transferForm.toPlace, null)
    }

    void reject(RejectCommand cmd, String userId, UUID workitemId) {
        TransferForm transferForm = TransferForm.get(cmd.id)
        domainStateMachineHandler.reject(transferForm, userId, Activities.APPROVE, cmd.comment, workitemId)
        transferForm.approver = Teacher.load(userId)
        transferForm.dateApproved = LocalDate.now()
    }
}
