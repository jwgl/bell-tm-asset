package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher

class UserArea {
    Teacher user
    String building

    static mapping = {
        comment '楼区管理员表'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        user comment: '用户'
        building comment: '管理的楼区'
    }

    static constraints = {
        user unique: 'building'
    }
}
