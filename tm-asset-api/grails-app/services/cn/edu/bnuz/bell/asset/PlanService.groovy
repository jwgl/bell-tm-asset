package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.dv.DvPlan
import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.master.TermService
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

@Transactional
class PlanService {
    LabelService labelService
    TermService termService
    PlaceService placeService

    def list(Integer termId) {
        DvPlan.executeQuery'''
select new map(
id as id,
action as action,
dateCreated as dateCreated,
dateProcessed as dateProcessed,
name as name,
status as status,
termId as termId,
roomRelative as roomRelative
)
from DvPlan
where :termId is null or termId = :termId
''', [termId: termId]
    }

    def getFormInfo(Long id) {
        def result = DvPlan.executeQuery'''
select new map(
id as id,
action as action,
dateCreated as dateCreated,
dateProcessed as dateProcessed,
name as name,
status as status,
termId as termId,
roomRelative as roomRelative,
info as info
)
from DvPlan
where id = :id
''', [id: id]
        return result ? result[0] : null
    }


    def getFormForEdit(Long id) {
        // 保留id在100以内的房间为特殊房间，不能编辑
        def result = Room.executeQuery'''
select new map(
r.id as id,
r.building as building,
r.name as name,
r.seat as seat,
r.measure as measure,
r.status as status,
r.note as note,
d.id as departmentId,
tp.level1 as groups,
tp.level2 as roomType,
tp.id as placeTypeId
)
from Room r
join r.department d
join r.placeType tp
where r.id = :id and r.id > 100
''', [id: id]
        if (result) {
            result[0]['labels'] = getRoomLabelsForPlan(id)
            def labels = labelService.labels()
            return [
                    form: result[0],
                    departments: Dept.findAll("from Dept order by name"),
                    placeTypes: RoomType.all,
                    labels: labels,
                    places: findPlaces(result[0].building, result[0].id),
                    termId: nextTerm,
                    buildings: placeService.buildings
            ]
        } else {
            throw new BadHttpRequest()
        }
    }

    def findPlaces(String building, Long id) {
        Room.executeQuery'''
select distinct new map(
id as id,
name as name,
measure as measure
)
from Room 
where id > 100 and building = :building and id <> :id
order by name''', [building: building, id: id]
    }

    def getNextTerm() {
        return termService.activeTerm.id % 10 == 1 ? termService.activeTerm.id + 1 : termService.activeTerm.id + 9
    }

    def createRoom(RoomCommand cmd) {
        Room form = new Room(
                name: Room.findByBuildingAndName(cmd.building, cmd.name) ? "${cmd.name}*" : cmd.name,
                building: cmd.building,
                seat: cmd.seat,
                measure: cmd.measure,
                status: cmd.status,
                note: cmd.note,
                department: Dept.load(cmd.departmentId),
                placeType: RoomType.load(cmd.placeTypeId)
        )
        if (!form.save(flush: true)){
            form.errors.each {
                println it
            }
            throw new BadRequestException("场地创建失败！")
        }
        if (cmd.labels?.size() > 0) {
            for (j in 0..cmd.labels.size() - 1) {
                RoomLabel item = new RoomLabel(
                        room: form,
                        label: Label.load(cmd.labels[j]),
                        dateCreated: new Date(),
                        dateExpired: new Date(2099, 1, 1)
                )
                item.save()
            }
        }
        return form
    }

    def create(PlanCommand cmd) {
        Plan plan = new Plan(
                name: cmd.name,
                action: cmd.action,
                status: 'CREATED',
                termId: getNextTerm(),
                info: "${cmd.rooms as JSON}",
                dateCreated: new Date()
        )
        if (!plan.save(flush: true)){
            plan.errors.each {
                println it
            }
        }
        if (cmd.relativePlaces?.size() > 0) {
            cmd.relativePlaces.each {
                PlanRoom planRoom = new PlanRoom(
                        plan: plan,
                        room: Room.load(it)
                )
                if (!planRoom.save()) {
                    planRoom.errors.each {
                        println it
                    }
                }
            }
        }
    }

    def executePlan(Plan plan, String op) {
        plan.setStatus(op)
        plan.setDateProcessed(new Date())
        if (!plan.save()) {
            plan.errors.each {
                println(it)
            }
        }
        List<PlanRoom> list = PlanRoom.findAllByPlan(plan)
        list?.each {
            it.room.setStatus('DELETED')
            it.room.save(flush: true)
        }
    }

    def getRoomLabelsForPlan(Long id) {
        RoomLabel.executeQuery'''
select new map(l.id as id)
from RoomLabel rl
join rl.label l
join l.type t
join l.creator u
where rl.room.id = :id
and (rl.deleted is null or deleted is false)
and current_date < rl.dateExpired
and t.single is not true
''', [id: id]
    }
}
