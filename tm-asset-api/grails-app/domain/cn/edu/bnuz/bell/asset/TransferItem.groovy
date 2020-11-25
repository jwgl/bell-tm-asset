package cn.edu.bnuz.bell.asset

class TransferItem {
    /**
     * 明细
     */
    Asset asset

    /**
     * 备注
     */
    String note

    static belongsTo = [transferForm: TransferForm]

    static mapping = {
        comment '流转单明细'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        asset comment: '设备明细'
        note type: 'text', comment: '备注'
    }

    static constraints = {
        note nullable: true
    }
}
