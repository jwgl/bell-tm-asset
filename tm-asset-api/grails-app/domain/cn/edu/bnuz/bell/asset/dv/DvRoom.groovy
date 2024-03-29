package cn.edu.bnuz.bell.asset.dv

import cn.edu.bnuz.bell.orm.PostgreSQLJsonUserType
import grails.converters.JSON

class DvRoom {
    Long id
    String name
    String building
    Integer seat
    Integer measure
    String status
    String department
    String groups
    String roomType
    Boolean planning
    JSON labels
    static mapping = {
        comment      '场地视图'
        table        name: 'dv_room', schema: 'tm_asset'
        name comment: '场地名称'
        building comment: '楼号'
        seat comment: '座位数'
        measure comment: '面积'
        department comment: '使用部门'
        groups comment: '一级分类'
        roomType comment: '二级分类'
        planning comment: '未完成计划'
        labels    type: PostgreSQLJsonUserType, comment: '标签'
    }
}
