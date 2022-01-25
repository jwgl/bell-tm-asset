package cn.edu.bnuz.bell.asset.eto

class PlaceUsageEto {
    String building
    String roomName
    Integer dayOfWeek
    Integer section
    Integer sectionCount
    String contact
    String department
    String courseName
    Integer termId
    Integer weekStart
    Integer weekEnd
    String evenOdd

    static mapping = {
        table   name: 'et_place_usage', schema: 'tm_asset'
        building type: 'text', comment: '楼号'
        roomName type: 'text', comment: '场地名'
        dayOfWeek comment: '星期几'
        section comment: '起始节'
        sectionCount comment: '长度'
        contact type: 'text', comment: '联系人'
        department type: 'text', comment: '部门'
        courseName type: 'text', comment: '课程名'
        termId comment: '学期'
        weekStart comment: '起始周'
        weekEnd comment: '结束周'
        evenOdd comment: '单双节'
    }
}
