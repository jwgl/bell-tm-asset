package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.stateMachine.*
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
class ReceiptApprovalService {
    ReceiptFormService receiptFormService
    @Resource(name='receiptReviewStateMachine')
    DomainStateMachineHandler domainStateMachineHandler
    LogService logService

    def getCounts(String userId) {
        [
                (ListType.TODO): Receipt.countByStatus(State.SUBMITTED),
                (ListType.DONE): Receipt.countByApprover(Teacher.load(userId))
        ]
    }

    def list(String userId, ListCommand cmd) {
        switch (cmd.type) {
            case ListType.TODO:
                return findTodoList(userId, cmd)
            case ListType.DONE:
                return findDoneList(userId, cmd)
            default:
                throw new BadRequestException()
        }
    }

    def findTodoList(String userId, ListCommand cmd) {
        def forms = Receipt.executeQuery'''
select new map(
    r.id as id,
    r.dateCheckIn as date,
    operator.name as operator,
    approver.name as approver,
    r.dateApproved as dateApproved,
    r.status as status,
    r.note as note
)
from Receipt r
join r.operator operator
left join r.approver approver
where r.status = :status
order by r.dateCheckIn desc
''', [status: State.SUBMITTED], [offset: cmd.offset, max: cmd.max]
        return [forms: forms, counts: getCounts(userId)]
    }

    def findDoneList(String userId, ListCommand cmd) {
        def forms = Receipt.executeQuery'''
select new map(
    r.id as id,
    r.dateCheckIn as dateCheckIn,
    operator.name as operator,
    approver.name as approver,
    r.dateApproved as date,
    r.status as status,
    r.note as note
)
from Receipt r
join r.operator operator
left join r.approver approver
where approver.id = :userId
order by r.dateCheckIn desc
''', [userId: userId], [offset: cmd.offset, max: cmd.max]
        return [forms: forms, counts: getCounts(userId)]
    }

    def getFormForReview(String userId, Long id, ListType type) {
        def form = receiptFormService.getFormInfo(id)

        def workitem = Workitem.findByInstanceAndActivityAndToAndDateProcessedIsNull(
                WorkflowInstance.load(form.workflowInstanceId),
                WorkflowActivity.load("${Receipt.WORKFLOW_ID}.${Activities.APPROVE}"),
                User.load(userId),
        )
        if (workitem) {
            form.workitemId = workitem.id
        }

        domainStateMachineHandler.checkReviewer(id, userId, Activities.APPROVE)

        return [
                form: form,
                counts: getCounts(userId),
                workitemId: workitem ? workitem.id : null,
        ]

    }

    def getFormForReview(String userId, Long id, ListType type, UUID workitemId) {
        def form = receiptFormService.getFormInfo(id)

        def activity = Workitem.get(workitemId).activitySuffix
        domainStateMachineHandler.checkReviewer(id, userId, activity)
        return [
                form: form,
                counts: getCounts(userId),
                workitemId: workitemId,
        ]
    }

    void accept(AcceptCommand cmd, String userId, UUID workitemId) {
        Receipt receipt = Receipt.get(cmd.id)
        domainStateMachineHandler.accept(receipt, userId, Activities.APPROVE, cmd.comment, workitemId)
        receipt.approver = Teacher.load(userId)
        receipt.dateApproved = LocalDate.now()
        receipt.items.each { item ->
            for (i in 1..item.pcs) {
                Asset asset = new Asset(
                    sn: item.pcs == 1 ? item.sn : null,
                    code: item.pcs == 1 ? item.code : null,
                        assetType: item.assetType,
                        name: item.name,
                        price: item.price,
                        dateBought: item.dateBought,
                        qualifyMonth: item.qualifyMonth,
                        supplier: item.supplier,
                        assetModel: item.assetModel,
                        unit: item.unit,
                        pcs: 1,
                        fault: false,
                        state: Status.STANDBY,
                        receipt: receipt,
                )
                if (!asset.save()) {
                    asset.errors.each {
                        println(it)
                    }
                }
            }
        }
        logService.log(Event.CHECKIN as String, "批准入库#${receipt.id}", null, null)
    }

    void reject(RejectCommand cmd, String userId, UUID workitemId) {
        Receipt receipt = Receipt.get(cmd.id)
        domainStateMachineHandler.reject(receipt, userId, Activities.APPROVE, cmd.comment, workitemId)
        receipt.approver = Teacher.load(userId)
        receipt.dateApproved = LocalDate.now()
    }
}
