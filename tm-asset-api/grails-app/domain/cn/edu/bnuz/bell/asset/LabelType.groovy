package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.User

/**
 * 标签类型
 */
class LabelType {
    /**
     * 类型名
     */
    String name

    /**
     * 是否私有
     */
    Boolean single

    /**
     * 标签颜色
     */
    String color

    /**
     * 创建者
     */
    User creator

    /**
     * 可多选
     */
    Boolean multiSelect

    static mapping = {
        comment '标签类型'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        name type: 'text', comment: '类型名'
        single comment: '是否私有'
        color type: 'text', comment: '标签颜色'
        creator comment: '创建者'
        multiSelect comment: '可多选'
    }

    static constraints = {
        name unique: 'creator'
    }
}
