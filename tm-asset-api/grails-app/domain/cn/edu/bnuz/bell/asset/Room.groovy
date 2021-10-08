package cn.edu.bnuz.bell.asset


class Room {

    /**
     * 场地名称
     */
    String name

    /**
     * 教学楼
     */
    String building

    /**
     * 座位数
     */
    Integer seat

    /**
     * 面积
     */
    Integer measure

    /**
     * 状态
     */
    String status

    /**
     * 桌椅类型
     */
    String seatType

    /**
     * 功能
     */
    String purpose

    /**
     * 使用部门
     */
    Dept department

    /**
     * 场地类别
     */
    RoomType placeType

    /**
     * 备注
     */
    String note

    static hasMany = [termStates: RoomLife]

    static mapping = {
        comment     '场地'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: '场地ID'
        name        type: 'text', comment: '场地名称'
        building    type: 'text', comment: '教学楼'
        seat        comment: '座位数'
        measure     comment: '面积'
        status      type: 'text', comment: '状态'
        seatType    type: 'text', comment: '桌椅类型'
        placeType   comment: '场地类别'
        purpose     type: 'text', comment: '功能'
        department  comment: '使用部门'
        note        length: 200, comment: '备注'
    }

    static constraints = {
        note nullable: true
        purpose nullable: true
        seatType nullable: true
        name unique: 'building'
    }
}
