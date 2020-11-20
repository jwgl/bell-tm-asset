package cn.edu.bnuz.bell.asset

class Supplier {

    /**
     * 名称
     */
    String name

    /**
     * 地址
     */
    String address

    /**
     * 联系人
     */
    String liaison

    /**
     * 联系电话
     */
    String phone
    static mapping = {
        comment '供应商'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        name type: 'text', comment: '名称'
        address type: 'text', comment: '地址'
        liaison type: 'text', comment: '联系人'
        phone type: 'text', comment: '联系电话'
    }
    static constraints = {
        name unique: true
        address nullable: true
        liaison nullable: true
        phone nullable: true
    }
}
