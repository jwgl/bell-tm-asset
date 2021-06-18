package cn.edu.bnuz.bell.asset.cn.edu.bnuz.bell.asset.dv

import cn.edu.bnuz.bell.orm.PostgreSQLJsonUserType
import grails.converters.JSON

class DvUserArea {
    String id
    String name
    JSON areas

    static mapping = {
        comment      '楼区管理员权限视图'
        table        name: 'dv_user_area', schema: 'tm_asset'
        areas    type: PostgreSQLJsonUserType
    }
}
