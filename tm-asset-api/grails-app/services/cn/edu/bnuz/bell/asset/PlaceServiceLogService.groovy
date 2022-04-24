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
    u.name as userName
)
from ServiceLog l
join l.department d
join l.user u
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
        println cmd.name
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
                dateFinished: LocalDate.parse(cmd.dateFinished),
                note: cmd.note
        )
        if (!form.save()) {
            form.errors.each {
                println it
            }
        }
        return form
    }
}
