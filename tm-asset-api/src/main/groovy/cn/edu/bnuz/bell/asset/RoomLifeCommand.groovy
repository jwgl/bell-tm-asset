package cn.edu.bnuz.bell.asset

class RoomLifeCommand {
    Long id

    /**
     * 场地名称
     */
    String name

    /**
     * 变动方式
     */
    String action

    /**
     * 分拆房间数
     */
    Integer counts

    /**
     * 合并相关房间
     */
    List<Long> otherPlaces

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
     * 标签
     */
    List<Long> labels

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
}
