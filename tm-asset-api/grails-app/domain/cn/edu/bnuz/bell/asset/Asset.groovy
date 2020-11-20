package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.stateMachine.*

import java.time.LocalDate

class Asset {

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
     * 报废日期
     */
    LocalDate dateForbid

    /**
     * 核销日期
     */
    LocalDate dateClose

    /**
     * 状态
     */
    Status state

    /**
     * 维修标志
     */
    Boolean fault

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

    /**
     * 入库单
     */
    Receipt receipt

    /**
     * 所在场所
     */
    Room room

    static mapping = {
        comment '资产'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        sn type: 'text', comment: 'sn编码'
        code type: 'text', comment: '资产编号'
        assetType type: 'text', comment: '资产类别'
        name type: 'text', comment: '设备名称'
        price comment: '单价'
        dateBought comment: '购买日期'
        qualifyMonth comment: '保质期'
        dateForbid comment: '报废日期'
        dateClose comment:'核销日期'
        state sqlType: 'tm_asset.state', type: StatusUserType, comment: '资产状态'
        fault comment:'是否需维修'
        supplier comment: '供应商'
        assetModel comment: '规格型号'
        total comment: '金额'
        unit type: 'text', comment: '单位'
        pcs comment:'数量'
        note type: 'text', comment: '备注'
        receipt comment: '原入库单'
        room comment: '所在场地'
    }

    static constraints = {
        code nullable: true
        assetType nullable: true
        supplier nullable: true
        dateBought nullable: true
        qualifyMonth nullable: true
        dateClose nullable: true
        dateForbid nullable: true
        sn nullable: true
        price nullable: true
        total nullable: true
        note nullable: true
        receipt nullable: true
        room nullable: true
        name nullable: true
    }

    Boolean canAction(Event event) {
        switch (state) {
            case Status.STANDBY:
                return event in ([Event.CHECKOUT, Event.FORBID, Event.LOSE, Event.REPAIR, Event.ALLOT] as Set<Event>)
            case Status.USING:
                return event in ([Event.STOP, Event.TRANSFER] as Set<Event>)
            case Status.REPAIRING:
                return event ==  Event.FIX
            case Status.OFF:
                return event == Event.CLOSE
            case Status.LOST:
                return event == Event.CLOSE
            default:
                return false
        }
    }

    Status passAction(Event event) {
        if (canAction(event)) {
            switch (state) {
                case Status.STANDBY:
                    switch (event) {
                        case Event.CHECKOUT:
                            return Status.USING
                        case Event.FORBID:
                            return Status.OFF
                        case Event.LOSE:
                            return Status.LOST
                        case Event.REPAIR:
                            return Status.REPAIRING
                        case Event.ALLOT:
                            return Status.STANDBY
                    }
                    break
                case Status.USING:
                    switch (event) {
                        case Event.STOP:
                            return Status.STANDBY
                        case Event.TRANSFER:
                            return Status.USING
                    }
                    break
                case Status.REPAIRING:
                    return Status.STANDBY
                case Status.OFF:
                    return Status.CLEARANCE
                case Status.LOST:
                    return Status.CLEARANCE
            }
        } else {
            return state
        }
    }


}
