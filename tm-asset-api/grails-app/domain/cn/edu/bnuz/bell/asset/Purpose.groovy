package cn.edu.bnuz.bell.asset

class Purpose {
    String name

    static mapping = {
        comment     '功能'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: 'ID'
        name        type: 'text', comment: '功能名称'
    }

    static constraints = {
        name unique: true
    }
}
