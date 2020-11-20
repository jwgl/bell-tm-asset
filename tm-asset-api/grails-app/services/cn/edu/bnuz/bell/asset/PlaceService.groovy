package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Department
import cn.edu.bnuz.bell.organization.DepartmentService
import grails.gorm.transactions.Transactional
import javassist.tools.web.BadHttpRequest

@Transactional
class PlaceService {
    DepartmentService departmentService

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
'''
        if (!cmd.criterion.isEmpty()) {
            sqlStr += " where ${cmd.criterion} order by r.building, r.name"
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
                department: Department.load(cmd.departmentId),
                placeType: RoomType.load(cmd.placeTypeId)
        )
        if (!form.save()){
            form.errors.each {
                println it
            }
        }
        return form
    }

    def getFormForCreate() {
        return [
                form: [],
                departments: departmentService.allDepartments,
                seatTypes: SeatType.findAll("from SeatType order by name"),
                purposes: Purpose.all,
                placeTypes: RoomType.findAll("from RoomType order by level1,level2"),
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
where r.id = :id
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
        Room.executeQuery("select distinct new map(building as building, name as name, name as value) from Room order by name")
    }
}
