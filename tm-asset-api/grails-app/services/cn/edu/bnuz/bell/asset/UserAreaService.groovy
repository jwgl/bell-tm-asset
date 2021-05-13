package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import grails.gorm.transactions.Transactional

@Transactional
class UserAreaService {
    PlaceService placeService

    def list() {
        UserArea.executeQuery'''
select new map(
    ua.id as id,
    u.name as name,
    r.building as building,
    r.name as roomName
)
from UserArea ua
join ua.user u
join ua.room r
order by u.name
'''
    }

    def getFormForCreate() {
        return [
                form: [:],
                rooms: placeService.list(null)
        ]
    }

    def create(UserAreaCommand cmd) {
        cmd.buildings?.each{
            UserArea userArea = UserArea.findByUserAndBuilding(Teacher.load(cmd.userId), it)
            if (!userArea) {
                UserArea form = new UserArea(
                        user: Teacher.load(cmd.userId),
                        building: it
                )
                if (!form.save()){
                    form.errors.each {
                        println it
                    }
                }
            }
        }
    }

    def delete(Long id) {
        UserArea userArea = UserArea.load(id)
        userArea?.delete()
    }
}
