package cn.edu.bnuz.bell.asset

class RoomLife {

    String name

    Integer seat

    Integer measure

    String status

    RoomType placeType

    Dept department

    String note

    Integer termId

    Date dateCreated

    static belongsTo = [room: Room]

    static mapping = {
        comment     '场地变动'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: 'ID'
        name        type: 'text', comment: '场地名称'
        seat        comment: '座位数'
        measure     comment: '面积'
        status      type: 'text', comment: '状态'
        placeType   comment: '场地类别'
        department  comment: '使用部门'
        note        type: 'text', comment: '备注'
        termId      comment: '变动学期'
        dateCreated comment: '创建时间'
    }

}
