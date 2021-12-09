package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.master.TermService
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

@Transactional
class PlanService {
    LabelService labelService
    TermService termService
    PlaceService placeService

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

    def getPlanList(Long roomId) {
        RoomLife.executeQuery'''
select new map(

)
'''
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

    def update(RoomLifeCommand cmd) {
//        Room room = Room.load(cmd.id)
        switch (cmd.action) {
            case 'CREATE':
                //创建新场地
                Room room = createRoom(cmd.rooms[0])
                createRoomLife(new RoomLifeCommand(
                        name: room.name,
                        seat: room.seat,
                        status: cmd.rooms[0].status,
                        measure: room.measure,
                        departmentId: room.department.id,
                        placeTypeId: room.placeType.id,
                        note: cmd.note,
                        labels: cmd.rooms[0].labels
                ), room)
                break
            case 'REMOVE':
                //创建新场地
                Room room = Room.load(cmd.id)
                createRoomLife(new RoomLifeCommand(
                        name: room.name,
                        seat: room.seat,
                        status: 'DELETED',
                        measure: room.measure,
                        departmentId: room.department.id,
                        placeTypeId: room.placeType.id,
                        note: cmd.note,
                        labels: cmd.rooms[0].labels
                ), room)
                break
            case 'SEPARATE':
                // 场地变动信息：取消原场地
                Room room = Room.load(cmd.id)
                createRoomLife(new RoomLifeCommand(
                        name: room.name,
                        seat: room.seat,
                        status: 'DELETED',
                        measure: room.measure,
                        departmentId: room.department.id,
                        placeTypeId: room.placeType.id,
                        note: cmd.note
                ), room)

                // 创建多个新场地
                for (i in 0..cmd.rooms.size() - 1) {
                    def form = createRoom(cmd.rooms[i])
                    createRoomLife(new RoomLifeCommand(
                            name: cmd.rooms[i].name,
                            seat: cmd.rooms[i].seat,
                            status: cmd.rooms[i].status,
                            measure: cmd.rooms[i].measure,
                            departmentId: cmd.rooms[i].departmentId,
                            placeTypeId: cmd.rooms[i].placeTypeId,
                            note: cmd.rooms[i].note,
                            labels: cmd.rooms[i].labels
                    ), form)
                }
                break
            case 'MERGE':
                if (cmd.otherPlaces && cmd.otherPlaces.size() > 0) {
                    Room room = createRoom(cmd.rooms[0])
                    createRoomLife(new RoomLifeCommand(
                            name: room.name,
                            seat: room.seat,
                            status: cmd.rooms[0].status,
                            measure: room.measure,
                            departmentId: room.department.id,
                            placeTypeId: room.placeType.id,
                            note: cmd.note,
                            labels: cmd.rooms[0].labels
                    ), room)

                    for (i in 0..cmd.otherPlaces.size() - 1) {
                        def roomI = Room.load(cmd.otherPlaces[i])
                        createRoomLife(new RoomLifeCommand(
                                name: roomI.name,
                                seat: roomI.seat,
                                status: 'DELETED',
                                measure: roomI.measure,
                                departmentId: roomI.department.id,
                                placeTypeId: roomI.placeType.id,
                                note: roomI.note
                        ), roomI)
                    }
                }
                break
            case 'OTHER':
                Room room = Room.load(cmd.id)
                createRoomLife(cmd, room)
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

    /**
     * 创建新场地，状态为RAW标识新建未启用，区别于BACKUP
     * @param cmd
     * @return
     */
    def createRoom(RoomCommand cmd) {
        Room form = new Room(
                name: cmd.name,
                building: cmd.building,
                seat: cmd.seat,
                measure: cmd.measure,
                status: 'RAW',
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

    def createRoomLife(RoomLifeCommand cmd, Room room) {
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
        if(!roomLife.save(flush: true)) {
            roomLife.errors.each {
                println it
            }
        }
        if (cmd.labels?.size() > 0) {
            for (j in 0..cmd.labels?.size() - 1) {
                def roomLifeILabel = new RoomLifeLabel(
                        roomLife: roomLife,
                        label: Label.load(cmd.labels[j]),
                        dateCreated: new Date()
                )
                roomLifeILabel.save()
            }
        }
        return roomLife
    }
}
