package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.StateUserType
import cn.edu.bnuz.bell.workflow.WorkflowInstance

import java.time.LocalDate

class Receipt implements StateObject {

    /**
     * 入库时间
     */
    LocalDate dateCheckIn

    /**
     * 经办人
     */
    Teacher operator

    /**
     * 审核人
     */
    Teacher approver

    /**
     * 审核时间
     */
    LocalDate dateApproved

    /**
     * 状态
     */
    State status

    /**
     * 备注
     */
    String note

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    static hasMany = [items: ReceiptItem]

    static mapping = {
        comment '入库单'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        dateCheckIn comment: '入库时间'
        operator comment: '经办人'
        approver comment: '审核人'
        dateApproved comment: '审核时间'
        note type: 'text', comment: '备注'
        status sqlType: 'tm.state', type: StateUserType, comment: '状态'
        workflowInstance comment: '工作流实例'
    }
    static constraints = {
        approver nullable: true
        dateApproved nullable: true
        note nullable: true
        workflowInstance nullable: true
    }

    String getWorkflowId() {
        WORKFLOW_ID
    }

    static final WORKFLOW_ID = 'asset.checkin'
}
