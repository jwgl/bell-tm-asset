package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.dv.DvUserArea
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

@Transactional
class UserAreaService {
    PlaceService placeService

    def list() {
        DvUserArea.executeQuery'''
select new map(
    u.id as id,
    u.name as name,
    u.areas as areas
)
from DvUserArea u
'''
    }

    def getFormForCreate() {
        return [
                form: [:],
                rooms: placeService.list(new RoomOptionCommand())
        ]
    }

    def create(UserAreaCommand cmd) {
        def user = User.load(cmd.userId)
        UserArea.executeUpdate("delete from UserArea where user = :user and room.id in (:ids)", [user: user, ids: cmd.rooms])
        UserArea.executeUpdate'''
insert into UserArea(user, room)
select :user, r from Room r where id in (:ids)
''', [user: user, ids: cmd.rooms]
    }

    def delete(String id) {
        def user = User.load(id)
        UserArea.executeUpdate("delete from UserArea where user = :user", [user: user])
    }
}
