package cn.edu.bnuz.bell.asset.dv

import cn.edu.bnuz.bell.orm.PostgreSQLJsonUserType
import grails.converters.JSON

class DvRoomWithLabel {
    Long id
    String name
    String building
    Integer seat
    Integer measure
    String status
    String purpose
    String seatType
    Long departmentId
    Long placeTypeId
    String department
    String groups
    String roomType
    Integer termId
    Boolean isReform
    JSON labels
    static mapping = {
        comment      '带学期的场地视图'
        table        name: 'dv_room_with_label', schema: 'tm_asset'
        id comment: '场地id'
        name comment: '场地名称'
        building comment: '楼号'
        seat comment: '座位数'
        measure comment: '面积'
        purpose comment: '功能'
        seatType comment: '座椅类型'
        departmentId comment: '使用部门'
        placeTypeId comment: '场地分类'
        department comment: '使用部门'
        groups comment: '一级分类'
        roomType comment: '二级分类'
        termId comment: '学期'
        isReform comment: '是否有改造计划'
        labels    type: PostgreSQLJsonUserType, comment: '标签'
    }
}
