package cn.edu.bnuz.bell.asset.dv

class DvPlan {
    Long id

    String name

    String status

    Integer termId

    Date dateCreated

    String action

    Date dateProcessed

    String roomRelative

    String info

    static mapping = {
        comment     '场地变动'
        table       name: 'dv_plan', schema: 'tm_asset'
        name        type: 'text', comment: '场地名称'
        status      type: 'text', comment: '状态'
        action      type: 'text', comment: '变更类型'
        termId      comment: '变动学期'
        dateCreated comment: '创建时间'
        dateProcessed comment: '处理时间'
        roomRelative     type: 'text', comment: '受影响场地名称'
        info      type: 'text', comment: '新场地信息JSON'
    }
}
