package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.gorm.transactions.Transactional

@Transactional
class LabelService {
    SecurityService securityService

    def labels() {
        Label.executeQuery'''
select new map(
l.id as id,
l.name as labelName,
l.business as business,
t.name as type,
t.color as color,
t.single as single,
t.multiSelect as multiSelect
)
from Label l join l.type t 
where t.single = false or l.creator = :user
''', [user: User.load(securityService.userId)]
    }

    def labelTypes() {
        LabelType.executeQuery'''
select new map(
id as id,
name as name,
color as color,
single as single,
multiSelect as multiSelect
)
from LabelType
where single = false or creator = :user
''', [user: User.load(securityService.userId)]
    }

    def createLabel(LabelCommand cmd) {
        def type = LabelType.load(cmd.typeId)
        if (!type) {
            throw new BadRequestException('标签类型不存在')
        }
        if (!securityService.hasRole('ROLE_ASSET_LABEL_ADMIN') && !type.single) {
            throw new BadRequestException('该用户角色不能创建系统标签')
        }
        Label label = new Label(
                name: cmd.name,
                dateCreated: new Date(),
                creator: User.load(securityService.userId),
                business: cmd.business,
                type: LabelType.load(cmd.typeId)
        )
        if (!label.save()) {
            label.errors.each {
                println it
            }
        }
        return label
    }

    def createLabelType(LabelCommand cmd) {
        if (!securityService.hasRole('ROLE_ASSET_LABEL_ADMIN')) {
            cmd.single = true
        }
        LabelType type = new LabelType(
                name: cmd.typeName,
                single: cmd.single,
                color: cmd.color,
                creator: User.load(securityService.userId),
                multiSelect: cmd.multiSelect
        )
        if (!type.save()) {
            type.errors.each {
                println it
            }
        }
        return type
    }

    def createRoomLabel(RoomLabelCommand cmd) {
        def label = Label.load(cmd.labelId)
        if (!securityService.hasRole('ROLE_ASSET_LABEL_ADMIN') && !label?.type?.single) {
            throw new BadRequestException('该用户角色只能使用用户标签')
        }
        RoomLabel form = new RoomLabel(
                label: Label.load(cmd.labelId),
                room: Room.load(cmd.roomId),
                dateExpired: cmd.dateExpire
        )
        if (!form.save()) {
            form.errors.each {
                println it
            }
        }
        return form
    }
}
