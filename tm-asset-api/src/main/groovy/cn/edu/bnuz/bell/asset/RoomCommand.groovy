package cn.edu.bnuz.bell.asset

class RoomCommand {
    Long id

    /**
     * 场地名称
     */
    String name

    /**
     * 教学楼
     */
    String building

    /**
     * 座位数
     */
    Integer seat

    /**
     * 面积
     */
    Integer measure

    /**
     * 状态
     */
    String status

    /**
     * 桌椅类型
     */
    String seatType

    /**
     * 功能
     */
    String purpose

    /**
     * 使用部门
     */
    Long departmentId

    /**
     * 场地类别
     */
    Long placeTypeId

    /**
     * 备注
     */
    String note

    /**
     * 标签
     */
    List<Long> labels

    String department
    String placeType
}
