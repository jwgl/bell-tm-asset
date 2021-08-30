package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

/**
 * 标签
 */
class Label {

    /**
     * 名称
     */
    String name

    /**
     * 创建时间
     */
    Date dateCreated

    /**
     * 业务类别
     */
    String business

    /**
     * 标签类型
     */
    LabelType type

    /**
     * 创建者
     */
    User creator

    static mapping = {
        comment '标签'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        name type: 'text', comment: '名称'
        dateCreated comment: '创建时间'
        business type: 'text', comment: '业务类别'
        type: comment: '标签类型'
        creator comment: '创建者'
    }

    static constraints = {
        name unique: 'type'
    }

}
