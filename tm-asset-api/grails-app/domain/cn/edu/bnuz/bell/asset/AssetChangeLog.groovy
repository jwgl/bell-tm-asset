package cn.edu.bnuz.bell.asset

class AssetChangeLog {
    /**
     * 原值JSON
     */
    String fromValue

    /**
     * 新值JSON
     */
    String toValue

    /**
     * 产生时间
     */
    Date dateCreated

    static belongsTo = [asset: Asset]

    static mapping = {
        comment '变更信息日志'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        fromValue type: 'text', comment: '原值JSON'
        toValue type: 'text', comment: '新值JSON'
        dateCreated comment: '产生时间'
    }
}
