package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.http.NotFoundException
import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.commands.SubmitCommand
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

import javax.annotation.Resource
import java.time.LocalDate

@Transactional
class ReceiptFormService {
    SecurityService securityService

    @Resource(name='receiptReviewStateMachine')
    DomainStateMachineHandler domainStateMachineHandler

    def list() {
        Receipt.executeQuery'''
select new map(
    r.id as id,
    r.dateCheckIn as dateCheckIn,
    operator.name as operator,
    approver.name as approver,
    r.dateApproved as dateApproved,
    r.status as status,
    r.note as note
)
from Receipt r
join r.operator operator
left join r.approver approver
order by r.dateCheckIn desc
'''
    }

    def getFormForCreate() {
        return [
                form: [],
                assetTypes: ReceiptItem.executeQuery("select distinct new map(r.assetType as name, r.assetType as value) from ReceiptItem r order by r.assetType"),
                suppliers: Supplier.executeQuery("select new map(s.id as id, s.name as name )from Supplier s order by s.name")
        ]
    }

    def findModels(String q) {
        AssetModel.executeQuery'''
select new map(
    am.id as id,
    am.name as name,
    am.brand as brand,
    am.specs as specs,
    am.parameter as parameter
)
from AssetModel am
where am.name like :q or am.brand like :q or am.specs like :q
order by am.name, am.brand
''', [q: "%${q}%"]
    }

    def create(ReceiptFormCommand cmd) {
        def form = new Receipt(
                note: cmd.note,
                operator: Teacher.load(securityService.userId),
                dateCheckIn: LocalDate.now(),
                status: domainStateMachineHandler.initialState
        )
        cmd.addedItems.each { item ->
            ReceiptItem receiptItem = new ReceiptItem(
                    sn: item.sn,
                    code: item.code,
                    name: item.name,
                    price: item.price,
                    dateBought: item.dateBought ? LocalDate.parse(item.dateBought) : null,
                    qualifyMonth: item.qualifyMonth,
                    supplier: Supplier.load(item.supplierId),
                    assetModel: AssetModel.load(item.assetModelId),
                    assetType: item.assetType,
                    unit: item.unit,
                    pcs: item.pcs,
                    total: item.pcs * item.price,
                    note: item.note
            )
            form.addToItems(receiptItem)
        }
        form.save(flush: true)
        domainStateMachineHandler.create(form, securityService.userId)
        return form
    }

    Map getFormInfo(Long id) {
        def result = Receipt.executeQuery'''
select new map(
    r.id as id,
    r.dateCheckIn as dateCheckIn,
    operator.name as operator,
    approver.name as approver,
    r.dateApproved as dateApproved,
    r.status as status,
    r.workflowInstance.id as workflowInstanceId,
    r.note as note
)
from Receipt r
join r.operator operator
left join r.approver approver
where r.id = :id
''', [id: id]
        if (result) {
            def form = result[0]
            form['editable'] = domainStateMachineHandler.canUpdate(form)
            form['items'] = ReceiptItem.executeQuery'''
select new map(
    ri.id as id,
    ri.sn as sn,
    ri.code as code,
    ri.name as name,
    ri.price as price,
    ri.dateBought as dateBought,
    ri.qualifyMonth as qualifyMonth,
    ri.assetType as assetType,
    ri.unit as unit,
    ri.pcs as pcs,
    ri.note as note,
    s.name as supplier,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from ReceiptItem ri
left join ri.assetModel m
left join ri.supplier s
where ri.receipt.id = :formId
''', [formId: id]
            return form
        } else {
            return [:]
        }
    }

    def getFormForEdit(Long id) {
        def result = Receipt.executeQuery'''
select new map(
    r.id as id,
    r.note as note
)
from Receipt r
where r.id = :id
''', [id: id]
        if (result) {
            def form = result[0]
            form['items'] = ReceiptItem.executeQuery'''
select new map(
    ri.id as id,
    ri.sn as sn,
    ri.code as code,
    ri.name as name,
    ri.price as price,
    ri.dateBought as dateBought,
    ri.qualifyMonth as qualifyMonth,
    ri.assetType as assetType,
    ri.unit as unit,
    ri.pcs as pcs,
    ri.total as total,
    m.id as assetModelId,
    m.brand as brand,
    m.specs as specs,
    m.parameter as parameter
)
from ReceiptItem ri
left join ri.assetModel m
where ri.receipt.id = :formId
''', [formId: id]
            return [
                    form: form,
                    assetTypes: ReceiptItem.executeQuery("select distinct new map(r.assetType as name, r.assetType as value) from ReceiptItem r order by r.assetType"),
                    suppliers: Supplier.executeQuery("select new map(s.id as id, s.name as name )from Supplier s order by s.name")
            ]
        } else {
            throw new BadHttpRequest()
        }
    }

    def update(ReceiptFormCommand cmd) {
        def form = Receipt.load(cmd.id)
        if (form && form.status == State.CREATED) {
            form.note = cmd.note
            def oldItems = ReceiptItem.findAllByReceipt(form)
            oldItems.each {
                it.delete()
                form.removeFromItems(it)
            }
            form.save(flush: true)
            cmd.addedItems.each { item ->
                println "supplierId: ${item.supplierId}"
                println Supplier.load(item.supplierId)?.name
                ReceiptItem receiptItem = new ReceiptItem(
                        sn: item.sn,
                        code: item.code,
                        name: item.name,
                        price: item.price,
                        dateBought: item.dateBought ? LocalDate.parse(item.dateBought) : null,
                        qualifyMonth: item.qualifyMonth,
                        supplier: Supplier.load(item.supplierId),
                        assetModel: AssetModel.load(item.assetModelId),
                        assetType: item.assetType,
                        unit: item.unit,
                        pcs: item.pcs,
                        total: item.pcs * item.price,
                        note: item.note
                )
                form.addToItems(receiptItem)
            }
            form.save(flush: true)
            return form
        }
    }

    def submit(SubmitCommand cmd) {
        Receipt form = Receipt.get(cmd.id)

        if (!form) {
            throw new NotFoundException()
        }

        if (!domainStateMachineHandler.canSubmit(form)) {
            throw new BadRequestException()
        }
        domainStateMachineHandler.submit(form, securityService.userId, cmd.to, cmd.comment, cmd.title)
        form.save()
    }
}
