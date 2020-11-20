package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User


class Log {
    /**
     * 操作人
     */
    User user

    /**
     * 操作时间
     */
    Date dateCreated

    /**
     * 操作场地
     */
    Room room

    /**
     * 操作设备
     */
    Asset asset

    /**
     * 动作
     */
    String action

    /**
     * 内容
     */
    String note

    /**
     * IP
     */
    String ip

    static mapping = {
        comment '日志'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        user comment: '操作人'
        dateCreated comment: '操作日期'
        room comment: '场地'
        asset comment: '设备'
        action type: 'text', comment: '动作'
        note type: 'text', comment: '内容'
        ip type: 'text', comment: '用户IP'
    }

    static constraints = {
        room nullable: true
        asset nullable: true
        note nullable: true
        ip nullable: true
    }
}
