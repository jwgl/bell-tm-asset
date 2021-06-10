package cn.edu.bnuz.bell.asset

import java.time.LocalDate

/**
 * 设备购物车
 */
class AssetCart {
    Asset asset
    String userId
    LocalDate dateCreated
    String name

    static mapping = {
        comment '设备购物车'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        userId comment: '所有人'
        dateCreated comment: '创建时间'
        name comment: '购物车名称'
    }

    static constraints = {
        name nullable: true
        dateCreated nullable: true
    }

    Boolean nameAble(String name) {
        findByName(name) == null
    }
}
