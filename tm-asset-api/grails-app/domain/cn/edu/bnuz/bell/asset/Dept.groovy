package cn.edu.bnuz.bell.asset

class Dept {
    Long id
    String name
    String tmDepartmentId
    static mapping = {
        comment '部门'
        table schema: 'tm_asset'
        id    generator: 'identity', comment: '部门ID'
        name length: 50, comment: '名称'
        tmDepartmentId length: 2, comment: '教务系统的部门ID'
    }
    static constraints = {
        tmDepartmentId nullable: true
    }
}
