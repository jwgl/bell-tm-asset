package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

class UserArea {
    User user
    String building

    static mapping = {
        comment '楼区管理员表'
        table schema: 'tm_asset'
        user comment: '用户'
        building comment: '管理的楼区'
    }

    static constraints = {
        user unique: 'building'
    }
}
