package cn.edu.bnuz.bell.asset

/**
 * 场地变动-标签
 */
class RoomLifeLabel implements Serializable {

    RoomLife roomLife
    Label label
    Date dateCreated

    static belongsTo = [roomLife: RoomLife]

    static mapping = {
        comment '标签'
        table schema: 'tm_asset'
        id composite: ['roomLife', 'label'], comment: '场地变动-标签'
        roomLife comment: '场地变动'
        label comment: '标签'
    }

    boolean equals(other) {
        if (!(other instanceof RoomLabel)) {
            return false
        }

        other.room?.id == roomLife?.id && other.label?.id == label?.id
    }

    int hashCode() {
        Objects.hash(roomLife.id, label.id)
    }
}
