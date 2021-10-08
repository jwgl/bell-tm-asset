package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.master.TermService
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

@Transactional
class RoomLifeService {
    LabelService labelService
    TermService termService

    def list(Integer termId) {
        Room.executeQuery'''
select new map(
r.id as id,
case when t.termId is null then r.name else t.name as name,
r.building as building,
r.seat as seat,
r.measure as measure,
r.status as status,
r.purpose as purpose,
r.note as note,
r.seatType as seatType,
d.name as department,
tp.level1 as groups,
tp.level2 as roomType
)
from Room r
join r.department d
join r.placeType tp
left join r.termStates t
where t.termId is null or t.termId = :termId
''', [termId: termId]
    }

    def getFormForEdit(Long id) {
        // 保留id在100以内的房间为特殊房间，不能编辑
        def result = Room.executeQuery'''
select new map(
r.id as id,
r.building as building,
(case when t.termId = :termId then t.name else r.name end) as name,
(case when t.termId = :termId then t.seat else r.seat end) as seat,
(case when t.termId = :termId then t.measure else r.measure end) as measure,
(case when t.termId = :termId then t.status else r.status end) as status,
(case when t.termId = :termId then t.note else r.note end) as note,
(case when t.termId = :termId then dt.id else d.id end) as departmentId,
(case when t.termId = :termId then tpt.level1 else tp.level1 end) as groups,
(case when t.termId = :termId then tpt.level2 else tp.level2 end) as roomType,
(case when t.termId = :termId then tpt.id else tp.id end) as placeTypeId
)
from Room r
join r.department d
join r.placeType tp
left join r.termStates t
left join t.placeType tpt
left join t.department dt
where r.id = :id and (t.termId is null or t.termId = :termId)
''', [id: id, termId: termService.activeTerm.id]
        if (result) {
            def labels = labelService.labels()
            return [
                    form: result[0],
                    departments: Dept.findAll("from Dept order by name"),
                    placeTypes: RoomType.all,
                    labels: labels,
                    places: findPlaces(result[0].building, result[0].id),
                    termId: nextTerm
            ]
        } else {
            throw new BadHttpRequest()
        }
    }

    def update(RoomLifeCommand cmd) {
        def room = Room.load(cmd.id)
        switch (cmd.action) {
            case 'SEPARATE':
                // 场地变动信息：取消原场地
                RoomLife roomLife = new RoomLife(
                        room: room,
                        name: room.name,
                        seat: room.seat,
                        status: 'DELETED',
                        termId: nextTerm,
                        measure: room.measure,
                        department: room.department,
                        placeType: room.placeType,
                        note: cmd.note,
                        dateCreated: new Date()
                )
                if(!roomLife.save()) {
                    roomLife.errors.each {
                        println it
                    }
                    throw new BadRequestException()
                }
                // 创建多个新场地，状态暂时为储备
                for (i in 1..cmd.counts) {
                    def form = new Room(
                            name: "${cmd.name}-${i}",
                            building: room.building,
                            seat: cmd.seat,
                            measure: cmd.measure,
                            status: 'BACKUP',
                            note: cmd.note,
                            department: Dept.load(cmd.departmentId),
                            placeType: RoomType.load(cmd.placeTypeId)
                    )
                    if (!form.save()){
                        form.errors.each {
                            println it
                        }
                        throw new BadRequestException()
                    }
                    def roomLifeI = new RoomLife(
                            room: form,
                            name: form.name,
                            seat: form.seat,
                            status: cmd.status,
                            termId: nextTerm,
                            measure: form.measure,
                            department: form.department,
                            placeType: form.placeType,
                            note: cmd.note,
                            dateCreated: new Date()
                    )
                    roomLifeI.save(flush: true)
                    // 场地变动标签
                    for (j in 0..cmd.labels?.size() - 1) {
                        def roomLifeILabel = new RoomLifeLabel(
                                roomLife: roomLifeI,
                                label: Label.load(cmd.labels[j]),
                                dateCreated: new Date()
                        )
                        roomLifeILabel.save()
                    }

                }
                break
            case 'MERGE':
                if (cmd.otherPlaces && cmd.otherPlaces.size() > 0) {
                    def form = new Room(
                            name: cmd.name,
                            building: room.building,
                            seat: cmd.seat,
                            measure: cmd.measure,
                            status: 'BACKUP',
                            note: cmd.note,
                            department: Dept.load(cmd.departmentId),
                            placeType: RoomType.load(cmd.placeTypeId)
                    )
                    form.save(flush: true)
                    def newFormRoomLife = new RoomLife(
                            room: form,
                            name: form.name,
                            seat: form.seat,
                            status: cmd.status,
                            termId: nextTerm,
                            measure: form.measure,
                            department: form.department,
                            placeType: form.placeType,
                            note: cmd.note,
                            dateCreated: new Date()
                    )
                    newFormRoomLife.save(flush: true)
                    // 场地变动标签
                    for (j in 0..cmd.labels?.size() - 1) {
                        def roomLifeILabel = new RoomLifeLabel(
                                roomLife: newFormRoomLife,
                                label: Label.load(cmd.labels[j]),
                                dateCreated: new Date()
                        )
                        roomLifeILabel.save()
                    }
                    for (i in 0..cmd.otherPlaces.size() - 1) {
                        def roomI = Room.load(cmd.otherPlaces[i])
                        def roomLifeI = new RoomLife(
                                room: roomI,
                                name: roomI.name,
                                seat: roomI.seat,
                                status: 'DELETED',
                                termId: nextTerm,
                                measure: roomI.measure,
                                department: roomI.department,
                                placeType: roomI.placeType,
                                note: cmd.note,
                                dateCreated: new Date()
                        )
                        if(!roomLifeI.save()) {
                            roomLifeI.errors.each {
                                println it
                            }
                            throw new BadRequestException()
                        }
                    }
                    RoomLife roomLife = new RoomLife(
                            room: room,
                            name: room.name,
                            seat: room.seat,
                            status: 'DELETED',
                            termId: nextTerm,
                            measure: room.measure,
                            department: room.department,
                            placeType: room.placeType,
                            note: cmd.note,
                            dateCreated: new Date()
                    )
                    if(!roomLife.save()) {
                        roomLife.errors.each {
                            println it
                        }
                    }
                }
                break
            case 'OTHER':
                RoomLife roomLife = new RoomLife(
                        room: room,
                        name: cmd.name,
                        seat: cmd.seat,
                        status: cmd.status,
                        termId: nextTerm,
                        measure: cmd.measure,
                        department: Dept.load(cmd.departmentId),
                        placeType: RoomType.load(cmd.placeTypeId),
                        note: cmd.note,
                        dateCreated: new Date()
                )
                if(!roomLife.save()) {
                    roomLife.errors.each {
                        println it
                    }
                }
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
}
