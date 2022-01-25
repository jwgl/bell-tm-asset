package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

import java.time.LocalDate

class ServiceLog {

    /**
     * 场地
     */
    Room room
    /**
     * 保修时间
     */
    LocalDate dateCreated
    Integer section
    /**
     * 报修人
     */
    String contact
    /**
     * 报修部门
     */
    Dept dept
    /**
     * 值班人
     */
    User user

    /**
     * 服务保障类型
     */
    String type
    /**
     * 服务保障项目
     */
    String item
    /**
     * 状态
     */
    String status
    /**
     * 完成时间
     */
    Date dateFinished
    /**
     * 备注
     */
    String note
    static mapping = {
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        room comment: '场地'
        dateCreated comment: '报修日期'
        section comment: '节次'
        contact type: 'text', comment: '报修人'
        dept type: 'text', comment: '报修部门'
        user comment: '值班人'
        type type: 'text', comment: '服务保障类型'
        item type: 'text', comment: '服务保障项目'
        status type: 'text', comment: '状态'
        dateFinished comment: '完成日期'
        note type: 'text', comment: '备注'
    }
    static constraints = {
        dateFinished nullable: true
        note nullable: true
    }
}
