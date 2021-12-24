package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

class HindField {
    User user
    String tableName
    String fields

    static mapping = {
        comment '不显示字段列表'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        user comment: '操作人'
        tableName type: 'text', comment: '表名'
        fields type: 'text', comment: '字段列表JSON'
    }
    static constraints = {
        fields nullable: true
    }
}
