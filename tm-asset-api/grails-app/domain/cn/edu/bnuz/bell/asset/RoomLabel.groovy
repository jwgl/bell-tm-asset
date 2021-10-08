package cn.edu.bnuz.bell.asset

/**
 * 场地标签
 */
class RoomLabel implements Serializable{

    Room room
    Label label
    Date dateCreated
    Date dateExpired
    Boolean deleted
    Date dateDeleted

    static belongsTo = [room: Room]

    static mapping = {
        comment '标签'
        table schema: 'tm_asset'
        id composite: ['room', 'label'], comment: '场地-标签'
        room comment: '场地'
        label comment: '标签'
        dateExpired comment: '失效时间'
        deleted comment: '删除标志'
        dateDeleted comment: '删除时间'
    }

    static constraints = {
        deleted nullable: true
        dateDeleted nullable: true
    }

    boolean equals(other) {
        if (!(other instanceof RoomLabel)) {
            return false
        }

        other.room?.id == room?.id && other.label?.id == label?.id
    }

    int hashCode() {
        Objects.hash(room.id, label.id)
    }
}
