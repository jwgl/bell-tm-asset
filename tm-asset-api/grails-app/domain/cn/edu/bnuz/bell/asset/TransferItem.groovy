package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.stateMachine.Status
import cn.edu.bnuz.bell.asset.stateMachine.StatusUserType

class TransferItem {
    /**
     * 明细
     */
    Asset asset

    /**
     * 备注
     */
    String note

    /**
     * 源地址
     */
    Room source

    /**
     * 原状态
     */
    Status state

    static belongsTo = [transferForm: TransferForm]

    static mapping = {
        comment '流转单明细'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        asset comment: '设备明细'
        note type: 'text', comment: '备注'
        source comment: '源地址'
        state sqlType: 'tm_asset.state', type: StatusUserType, comment: '资产状态'
    }

    static constraints = {
        note nullable: true
        source nullable: true
    }
}
