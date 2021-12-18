package cn.edu.bnuz.bell.asset

class Plan {

    String name

    String status

    Integer termId

    Date dateCreated

    String action

    Date dateProcessed

    /**
     * 新场地信息
     */
    String info


    static mapping = {
        comment     '场地变动'
        table       schema: 'tm_asset'
        id          generator: 'identity', comment: 'ID'
        name        type: 'text', comment: '场地名称'
        status      type: 'text', comment: '状态'
        action      type: 'text', comment: '变更类型'
        termId      comment: '变动学期'
        dateCreated comment: '创建时间'
        dateProcessed comment: '处理时间'
        info      type: 'text', comment: '新场地信息JSON'
    }

    static constraints = {
        dateProcessed nullable: true
    }

}
