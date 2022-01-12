package cn.edu.bnuz.bell.asset.dv

class DvHuisBooking {
    Long id
    String contact
    String contactNumber
    String subject
    String roomAndTime
    String workflowState

    static mapping = {
        comment     '场地变动'
        table       name: 'dv_huis_booking', schema: 'tm_asset'
        contact        type: 'text', comment: '联系人'
        contactNumber      type: 'text', comment: '联系电话'
        workflowState      type: 'text', comment: '状态'
        subject      comment: '主题'
        roomAndTime comment: '借用地点时间'
    }
}
