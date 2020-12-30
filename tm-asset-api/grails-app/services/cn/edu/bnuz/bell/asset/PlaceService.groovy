package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.DepartmentService
import cn.edu.bnuz.bell.security.SecurityService
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

@Transactional
class PlaceService {
    SecurityService securityService
    DepartmentService departmentService
    LogService logService

    def list(RoomOptionCommand cmd) {
        def sqlStr = '''
select new map(
r.id as id,
r.name as name,
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
where r.id not between 2 and 5
'''
        if (!cmd.criterion.isEmpty()) {
            sqlStr += " and ${cmd.criterion} order by r.building, r.name"
        }
        Room.executeQuery sqlStr, cmd.args
    }

    def create(RoomCommand cmd) {
        def form = new Room(
                name: cmd.name,
                building: cmd.building,
                seat: cmd.seat,
                measure: cmd.measure,
                status: cmd.status,
                purpose: cmd.purpose,
                note: cmd.note,
                seatType: cmd.seatType,
                department: Dept.load(cmd.departmentId),
                placeType: RoomType.load(cmd.placeTypeId)
        )
        if (!form.save()){
            form.errors.each {
                println it
            }
        }
        return form
    }

    def update(RoomCommand cmd) {
        Room form = Room.load(cmd.id)
        Map change = [
                from: [
                    seat: form.seat,
                    measure: form.measure
                ],
                to: [
                    seat: cmd.seat,
                    measure: cmd.measure
                ]
        ]
        logService.log('UPDATE', "${change as JSON}", form, null)
        form.setSeat(cmd.seat)
        form.setMeasure(cmd.measure)
        if (securityService.hasPermission('PERM_ASSET_PLACE_WRITE')) {
            form.setName(cmd.name)
            form.setBuilding(cmd.building)
            form.setStatus(cmd.status)
            form.setPurpose(cmd.purpose)
            form.setNote(cmd.note)
            form.setSeatType(cmd.seatType)
            form.setDepartment(Dept.load(cmd.departmentId))
            form.setPlaceType(RoomType.load(cmd.placeTypeId))
        }
        form.save(flush: true)
    }

    def getFormForCreate() {
        return [
                form: [],
                departments: Dept.findAll("from Dept order by name"),
                seatTypes: SeatType.findAll("from SeatType order by name"),
                purposes: Purpose.all,
                placeTypes: RoomType.findAll("from RoomType where id < 33 or id > 37 order by level1,level2"),
                buildings: buildings
        ]
    }

    def  getFormInfo(Long id) {
        def result = Room.executeQuery'''
select new map(
r.id as id,
r.name as name,
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
where r.id = :id
''', [id: id]
        return result ? result[0] : null
    }

    def getFormForEdit(Long id) {
        // 保留id在100以内的房间为特殊房间，不能编辑
        def result = Room.executeQuery'''
select new map(
r.id as id,
r.name as name,
r.building as building,
r.seat as seat,
r.measure as measure,
r.status as status,
r.purpose as purpose,
r.note as note,
r.seatType as seatType,
d.id as departmentId,
tp.level1 as groups,
tp.level2 as roomType,
tp.id as placeTypeId
)
from Room r
join r.department d
join r.placeType tp
where r.id = :id and r.id >100
''', [id: id]
        if (result) {
            return [
                    form: result[0],
                    departments: departmentService.allDepartments,
                    seatTypes: SeatType.all,
                    purposes: Purpose.all,
                    placeTypes: RoomType.all,
                    buildings: buildings
            ]
        } else {
            throw new BadHttpRequest()
        }
    }

    def getBuildings() {
        Room.executeQuery("select distinct new map(building as name, building as value) from Room order by building")
    }

    def getPlaces() {
        Room.executeQuery'''
select distinct new map(
id as id,
building as building,
name as name,
name as value
)
from Room 
where not id between 2 and 5
order by name'''
    }

    def delete(Long id) {
        Room room = Room.load((id))
        if (!room) {
            throw new BadHttpRequest()
        }
        logService.log('DELETE', "${room as JSON}", null, null)
        room.delete()
    }
}
