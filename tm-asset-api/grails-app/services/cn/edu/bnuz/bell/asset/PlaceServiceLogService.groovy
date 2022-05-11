package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.eto.PlaceUsageEto
import cn.edu.bnuz.bell.master.Term
import cn.edu.bnuz.bell.master.TermService
import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.DepartmentService
import cn.edu.bnuz.bell.place.Place
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Transactional
class PlaceServiceLogService {
    TermService termService
    DepartmentService departmentService
    SecurityService securityService

    def list() {
        ServiceLog.executeQuery'''
select new map(
    l.id as id,
    l.building as building,
    l.roomName as roomName,
    l.logDate as logDate,
    l.section as section,
    d.name as departmentName,
    l.contact as contact,
    l.type as type,
    l.item as item,
    l.status as status,
    l.dateFinished as dateFinished,
    l.note as note,
    max(t.id) as termId,
    u.name as userName
)
from ServiceLog l
join l.department d
join l.user u
, Term t
where t.id >= 20201 and l.logDate >= t.startDate
group by l.id, l.building, l.roomName, l.logDate, l.section, 
d.name, l.contact, l.type, l.item, l.status, l.dateFinished, l.note, u.name
'''
    }

    def getFormForCreate() {
        Term term = termService.activeTerm
        return [startDate: term.startDate, form: [:], departments: departmentService.allDepartments]
    }

    def findContact(PlaceUsageCommand cmd) {
        Term term = termService.activeTerm
        LocalDate logDate = LocalDate.parse(cmd.logDate)
        def week = (term.startDate.until(logDate, ChronoUnit.DAYS) / 7 +1) as Integer
        def dayOfWeek = logDate.dayOfWeek.value
        def odd = week % 2 == 1 ? '单' : '双'
        def result = PlaceUsageEto.executeQuery'''
select new map(
contact as contact,
department as department
)
from PlaceUsageEto
where termId = :termId
and roomName = :roomName
and :week between weekStart and weekEnd
and dayOfWeek = :dayOfWeek
and (evenOdd is null or evenOdd = :odd)
and :section between section and section + sectionCount - 1
''',[termId: term.id, roomName: cmd.roomName, week: week, dayOfWeek: dayOfWeek, odd: odd, section: cmd.section]
        return result ? result[0] : [:]
    }

    def findPlace(String placeName){
        Place.executeQuery'''
select new map(
    p.id as id,
    p.name as name
)
 from Place p where p.enabled = true and p.isExternal=false and p.name like :query
''',[query: "%${placeName}%"],[max: 10]
    }

    def findAsset(PlaceUsageCommand cmd) {
        Asset.executeQuery'''
select new map(
a.name as name,
m.brand as brand,
m.specs as specs,
m.parameter as parameter
)
from Asset a
left join a.assetModel m
left join a.room r
where r.building = :building and r.name = :name and a.state = 'USING'
''', [building: cmd.building, name: cmd.name]
    }

    def create(ServiceLogCommand cmd) {
        ServiceLog form = new ServiceLog(
                building: cmd.building,
                roomName: cmd.roomName,
                dateCreated: LocalDate.now(),
                logDate: LocalDate.parse(cmd.logDate),
                section: cmd.section,
                department: Department.load(cmd.departmentId),
                contact: cmd.contact,
                user: User.load(securityService.userId),
                type: cmd.type,
                item: cmd.item,
                status: cmd.dateFinished ? '完成' : '待完成',
                dateFinished: cmd.dateFinished ? LocalDate.parse(cmd.dateFinished) : null,
                note: cmd.note
        )
        if (!form.save()) {
            form.errors.each {
                println it
            }
        }
        return form
    }

    def update(ServiceLogCommand cmd) {
        ServiceLog form = ServiceLog.load(cmd.id)
        form.setBuilding(cmd.building)
        form.setRoomName(cmd.roomName)
        form.setLogDate(LocalDate.parse(cmd.logDate))
        form.setSection(cmd.section)
        form.setDepartment(Department.load(cmd.departmentId))
        form.setType(cmd.type)
        form.setItem(cmd.item)
        form.setStatus(cmd.dateFinished ? '完成' : '待完成')
        form.setDateFinished(cmd.dateFinished ? LocalDate.parse(cmd.dateFinished) : null)
        form.setNote(cmd.note)
        form.save(flush: true)
    }

    def getFormInfo(Long id) {
        def result = ServiceLog.executeQuery'''
select new map(
s.id as id,
s.building as building,
s.roomName as roomName,
s.logDate as logDate,
s.section as section,
d.name as departmentName,
s.contact as contact,
s.type as type,
s.item as item,
s.user.id as userId,
s.status as status,
s.note as note
)
from ServiceLog s join s.department d
where s.id = :id
''', [id: id]
        return [
                form: result[0],
                createAble: result[0].userId == securityService.userId && result[0].status == '待完成'
        ]
    }

    def getFormForEdit(Long id) {
        Term term = termService.activeTerm
        def result = ServiceLog.executeQuery'''
select new map(
s.id as id,
s.building as building,
s.roomName as roomName,
s.logDate as logDate,
s.section as section,
s.department.id as department,
s.contact as contact,
s.type as type,
s.item as item,
s.note as note
)
from ServiceLog s
where s.id = :id and s.user.id = :userId and s.dateFinished is null
''', [id: id, userId: securityService.userId]
        return [startDate: term.startDate, form: result[0], departments: departmentService.allDepartments]
    }

    def isEditor() {
        def result = PlaceKeeper.executeQuery("select a from PlaceKeeper a where a.user.id = :id and grantString like '%W%'", [id: securityService.userId])
        return result?.size() > 0
    }
}
