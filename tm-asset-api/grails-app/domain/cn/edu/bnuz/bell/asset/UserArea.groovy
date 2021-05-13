package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher

class UserArea {
    Teacher user
    Room room

    static mapping = {
        comment '楼区管理员表'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        user comment: '用户'
        room comment: '可管理的房间'
    }

    static constraints = {
        user unique: 'room'
    }
}
