package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.organization.Teacher
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.StateUserType
import cn.edu.bnuz.bell.workflow.WorkflowInstance

import java.time.LocalDate

class TransferForm implements StateObject {

    /**
     * 流转单类型
     */
    TransferType transferType

    /**
     * 提交时间
     */
    LocalDate dateSubmitted

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
     * 关联信息
     */
    String otherInfo

    /**
     * 源地址
     */
    Room fromPlace

    /**
     * 目标地址
     */
    Room toPlace

    /**
     * 工作流实例
     */
    WorkflowInstance workflowInstance

    static hasMany = [items: TransferItem]

    static mapping = {
        comment '流转单'
        table schema: 'tm_asset'
        id generator: 'identity', comment: 'ID'
        dateSubmitted comment: '提交时间'
        operator comment: '经办人'
        approver comment: '审核人'
        dateApproved comment: '审核时间'
        note type: 'text', comment: '备注'
        status sqlType: 'tm.state', type: StateUserType, comment: '状态'
        workflowInstance comment: '工作流实例'
        transferType comment: '流转单类型'
        otherInfo type: 'text', comment: '关联信息JSON'
        fromPlace comment: '源地址'
        toPlace comment: '目标地址'
    }
    static constraints = {
        approver nullable: true
        dateApproved nullable: true
        note nullable: true
        workflowInstance nullable: true
        otherInfo nullable: true
    }

    String getWorkflowId() {
        WORKFLOW_ID
    }

    static final WORKFLOW_ID = 'asset.transfer'
}
