package cn.edu.bnuz.bell.asset

class RoomType {
    /**
     * 一级名称
     */
    String level1

    /**
     * 二级名称
     */
    String level2

    /**
     * 编辑锁
     */
    Boolean locked

    /**
     * 资产默认状态
     */
    String state

    static mapping = {
        comment     '场地类别'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: 'ID'
        level1      type: 'text', comment: '一级名称'
        level2      type: 'text', comment: '二级名称'
        locked      comment: '编辑锁'
        state       type: 'text', comment: '资产默认状态'
    }

    static constraints = {
        level2 unique: 'level1'
    }
}
