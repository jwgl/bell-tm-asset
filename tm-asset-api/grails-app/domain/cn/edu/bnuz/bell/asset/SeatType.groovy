package cn.edu.bnuz.bell.asset

class SeatType {
    String name

    static mapping = {
        comment     '桌椅类型'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: 'ID'
        name        type: 'text', comment: '类型名称'
    }

    static constraints = {
        name unique: true
    }
}
