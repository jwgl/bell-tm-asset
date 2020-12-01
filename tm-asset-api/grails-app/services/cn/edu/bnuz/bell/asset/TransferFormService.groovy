package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.stateMachine.Status
import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

import javax.annotation.Resource
import java.time.LocalDate

@Transactional
class TransferFormService {
    @Resource(name='receiptReviewStateMachine')
    DomainStateMachineHandler domainStateMachineHandler
    LogService logService

    def list(userId) {
        TransferForm.executeQuery'''
select new map(
    tf.id as id,
    tf.note as note,
    tf.dateSubmitted as dateSubmitted,
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
where o.id = :userId
''', [userId: userId]
    }

    def create(String userId, TransferFormCommand cmd) {
        TransferType type = TransferType.findByName(cmd.transferType)
        Room toPlace = Room.load(cmd.toId)
        def form = new TransferForm(
                note: cmd.note,
                operator: Teacher.load(userId),
                dateSubmitted: LocalDate.now(),
                fromPlace: Room.load(cmd.fromId),
                transferType: type,
                toPlace: toPlace,
                status: type.action == 'TRANSFER' ? State.FINISHED : domainStateMachineHandler.initialState
        )
        cmd.addedItems.each { item ->
            Asset asset = Asset.load(item.id)
            if (type.action == 'TRANSFER') {
                asset.setRoom(toPlace)
                asset.setState(toPlace.placeType.state as Status)
                asset.save()
            }
            TransferItem transferItem = new TransferItem(
                    asset: asset,
                    note: item.note,
                    source: asset.room,
                    state: asset.state
            )
            form.addToItems(transferItem)
        }
        if (!form.save()) {
            form.errors.each {
                println(it)
            }
        }
        if (type.action != 'TRANSFER') {
            domainStateMachineHandler.create(form, userId)
        } else {
            logService.log(form.transferType.name, "${form.transferType.name}#${form.id}", form.toPlace, null)
        }
        return form
    }

    Map getFormInfo(Long id) {
        def result = TransferForm.executeQuery'''
select new map(
    tf.id as id,
    tf.note as note,
    tf.dateSubmitted as dateSubmitted,
    tf.dateApproved as dateApproved,
    tf.otherInfo as otherInfo,
    tf.status as status,
    tf.workflowInstance.id as workflowInstanceId,
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
where tf.id = :id
''', [id: id]
        if (result) {
            def form = result[0]
            form['editable'] = domainStateMachineHandler.canUpdate(form)
            form['items'] = TransferItem.executeQuery'''
select new map(
    a.id as id,
    a.sn as sn,
    a.code as code,
    a.name as name,
    a.price as price,
    a.dateBought as dateBought,
    a.qualifyMonth as qualifyMonth,
    a.assetType as assetType,
    a.unit as unit,
    a.pcs as pcs,
    a.note as note,
    tfi.state as state,
    s.name as supplier,
    r.building as building,
    r.name as place,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from TransferItem tfi
join tfi.asset a
left join a.assetModel m
left join a.supplier s
left join tfi.source r
where tfi.transferForm.id = :formId
''', [formId: id]
            return form
        } else {
            return [:]
        }
    }

    def submit(String userId, SubmitCommand cmd) {
        TransferForm form = TransferForm.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canSubmit(form)) {
            throw new BadRequestException()
        }
        logService.log(form.transferType.name, "申请${form.transferType.name}#${form.id}", form.toPlace, null)
        domainStateMachineHandler.submit(form, userId, cmd.to, cmd.comment, cmd.title)
        form.save()
    }

    def delete(Long id) {
        TransferForm form = TransferForm.load(id)
        if (!form) {
            throw new BadHttpRequest()
        }
        form.items.each {
            it.delete()
        }
        form.delete()
    }
}
