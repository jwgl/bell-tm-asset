package cn.edu.bnuz.bell.asset

import java.time.LocalDate

class ReceiptItem {

    /**
     *编码
     */
    String sn

    /**
     * 资产编号
     */
    String code

    /**
     * 资产类别
     */
    String assetType

    /**
     * 设备名称
     */
    String name

    /**
     * 单价
     */
    BigDecimal price

    /**
     * 数量
     */
    Integer pcs

    /**
     * 金额
     */
    BigDecimal total

    /**
     * 单位
     */
    String unit

    /**
     * 购置时间
     */
    LocalDate dateBought

    /**
     * 保修期
     */
    Integer qualifyMonth

    /**
     * 供应商
     */
    Supplier supplier

    /**
     * 规格型号
     */
    AssetModel assetModel

    /**
     * 备注
     */
    String note

    static belongsTo = [receipt: Receipt]

    static mapping = {
        comment '入库单明细'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        sn type: 'text', comment: 'sn编码'
        code type: 'text', comment: '资产编号'
        assetType type: 'text', comment: '资产类别'
        name type: 'text', comment: '设备名称'
        price comment: '单价'
        dateBought comment: '购买日期'
        qualifyMonth comment: '保质期'
        supplier comment: '供应商'
        assetModel comment: '规格型号'
        total comment: '金额'
        unit type: 'text', comment: '单位'
        pcs comment:'数量'
        note type: 'text', comment: '备注'
    }
    static constraints = {
        assetType nullable: true
        supplier nullable: true
        sn nullable: true
        code nullable: true
        price nullable: true
        qualifyMonth nullable: true
        total nullable: true
        note nullable: true
        dateBought nullable: true
        name nullable: true
        assetModel nullable: true
    }
}
