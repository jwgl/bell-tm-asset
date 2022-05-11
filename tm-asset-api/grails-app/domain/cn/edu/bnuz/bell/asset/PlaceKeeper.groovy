package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

class PlaceKeeper {
    User user
    String grantString

    static mapping = {
        comment '教室维修用户'
        table schema: 'tm_asset'
         user generator: 'assigned', comment: '值班人员'
        grantString type: 'text', comment: '权限'
    }

    static constraints = {
        user unique: true
    }
}
