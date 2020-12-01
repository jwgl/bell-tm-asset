package cn.edu.bnuz.bell.asset

class TransferType {
    String name
    String action

    static mapping = {
        comment '流转单类型'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        name type: 'text', comment: '类型名称'
        action type: 'text', comment: '操作'
    }
}
